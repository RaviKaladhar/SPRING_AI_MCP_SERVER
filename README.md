# Spring AI MCP Server

This repository contains a Model Context Protocol (MCP) Server built with Java and Spring Boot. It acts as the backend tool provider in an agentic AI ecosystem, exposing local database functionalities, RAG capabilities, and external APIs as standardized, machine-readable tools for Large Language Models (LLMs).

This server is designed to work in tandem with my [Spring AI MCP Client](https://github.com/RaviKaladhar/SPRING_AI_MCP_CLIENT).

## Advanced Features Implemented
* **Bi-directional Sampling:** The server can delegate sub-tasks (like summarizing ticket data) back to the connected LLM client mid-execution using `@McpSampling`.
* **Structured Elicitation:** If a tool requires missing parameters (e.g., a phone number to create a ticket), the server pauses execution and elicits the structured data from the client using `ctx.elicit()`.
* **Progress Tracking:** Long-running tool operations report their state asynchronously back to the client via `@McpProgress`.
* **Retrieval-Augmented Generation (RAG):** Integrates `VectorStore` to perform semantic similarity searches over document history.

## Tech Stack
* Java 21
* Spring Boot
* Spring AI (VectorStore)
* Model Context Protocol (MCP) SDK

## How to Run Locally
1. Clone the repository: `git clone https://github.com/RaviKaladhar/SPRING_AI_MCP_SERVER.git`
2. Navigate into the project: `cd SPRING_AI_MCP_SERVER`
3. Build and run: `mvn spring-boot:run`
