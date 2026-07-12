package com.project.springAI.tools;

import com.project.springAI.model.TicketInputRequest;
import com.project.springAI.repository.TicketRequestRepository;
import com.project.springAI.entity.TicketRequest;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.ai.mcp.annotation.context.StructuredElicitResult;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.document.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MCPServerTools {

    private final VectorStore vectorStore;

    private final TicketRequestRepository ticketRequestRepository;

    public MCPServerTools(VectorStore vectorStore, TicketRequestRepository ticketRequestRepository) {
        this.vectorStore = vectorStore;
        this.ticketRequestRepository = ticketRequestRepository;
    }

    @McpTool(
            name = "retrieveCricketHistory",
            description = "Returns the most relevant information for questions related to the Cricket History based on the user query."
    )
    public String retrieveCricketHistory(@McpToolParam String query, McpSyncRequestContext ctx) {
        ctx.info("retrieveCricketHistory tool is called with the following query: " + query);
        return vectorStore.similaritySearch(query).stream().map(Document::getText).collect(Collectors.joining(","));
    }

    @McpTool(name = "currentDate", description = "Returns the current date in the specified time zone.")
    LocalDate currentDate(@McpToolParam String zoneId, McpSyncRequestContext ctx) throws InterruptedException {
        ctx.info("currentDate is called with the following zoneId: " + zoneId);
        for(int i=0; i < 10; i++) {
            Thread.sleep(1000);
            double progress = (i+1) * 10;
            ctx.progress(progressSpec -> progressSpec.message("for the currentDate tool").progress(progress));
        }
        return LocalDate.now(ZoneId.of(zoneId));
    }

    @McpTool(name = "currentTime", description = "Returns the current time in the specified zone.")
    LocalDateTime currentTime(@McpToolParam ZoneId zoneId, McpSyncRequestContext ctx) {
        ctx.info("currentTime is called with the following zoneId: " + zoneId);
        return LocalDateTime.now(zoneId);
    }

    @McpTool(name = "ticketSummary", description = "Returns a summary of the all the tickets belongs to the given user, including issue, issue time, ETA, status, contact phone, and severity.")
    String ticketSummary(@McpToolParam String username, McpSyncRequestContext ctx) {
        ctx.info("ticketSummary is called with the following username: " + username);
        List<TicketRequest> tickets = ticketRequestRepository.findTicketsByUserName(username);

        String ticketSummary = tickets.stream()
                .map(ticket -> String.format("Issue: %s, Issue Time: %s, ETA: %s, Status: %s, Contact Phone: %s, Severity: %s",
                        ticket.getIssue(),
                        ticket.getIssueTime(),
                        ticket.getETA(),
                        ticket.getStatus(),
                        ticket.getContactPhone(),
                        ticket.getSeverity()))
                .collect(Collectors.joining("\n"));;
        if (ctx.sampleEnabled()) {
            ctx.info("Calling client APP for summary");
            McpSchema.CreateMessageResult result = ctx.sample(
                    samplingSpec -> samplingSpec.systemPrompt("Summarise the complete ticket details in a concise manner. " +
                                    "Do not miss any important information. " +
                                    "The summary should be in a format that is easy to read and understand and in a professional tone.")
                            .message(ticketSummary)
                    );

            ctx.info("Received the following summary from client APP: " + result.content());
            McpSchema.TextContent textContent = (McpSchema.TextContent) result.content();
            return textContent.text();
        }
        ctx.warn("Sampling is not enabled by Application. Returning the complete ticket details as is.");
        return ticketSummary;
    }

    @McpTool(name = "createTicket", description = "Creates a new ticket for the given user with the provided details.")
    void createTicket(@McpToolParam String username, @McpToolParam String issue, McpSyncRequestContext ctx) {
        ctx.info("createTicket is called with the following username: " + username + " and issue: " + issue);
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setUserName(username);
        ticketRequest.setIssue(issue);
        ticketRequest.setIssueTime(LocalDateTime.now());
        ticketRequest.setStatus("OPEN");

        if (ctx.elicitEnabled()) {

            ctx.info("Need details of contact and severity. Calling client APP for elicitation.");

            StructuredElicitResult<TicketInputRequest> result = ctx.elicit(elicitationSpec -> elicitationSpec.message("Please provide the contact phone number and severity level for the ticket."),
                    TicketInputRequest.class);

            if (result.action() == McpSchema.ElicitResult.Action.DECLINE) {
                ctx.warn("Elicitation is declined by Application. Using default values for contact phone and severity.");
                ticketRequest.setContactPhone("N/A");
                ticketRequest.setSeverity("MEDIUM");
            } else {
                ctx.info("Received the following details from client APP: " + result.structuredContent());
                ticketRequest.setContactPhone(result.structuredContent().contactPhone());
                ticketRequest.setSeverity(result.structuredContent().severity());
            }

        } else {
            ctx.warn("Elicitation is not enabled by Application. Using default values for contact phone and severity.");
            ticketRequest.setContactPhone("N/A");
            ticketRequest.setSeverity("MEDIUM");
        }

        ticketRequestRepository.save(ticketRequest);

        ctx.info("Created the ticket for the given user: " + username);
    }
}
