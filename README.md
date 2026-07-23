# paymenthub-ee-connector-channel

The inbound API entry point of Mifos Payment Hub EE: it receives payment and transfer requests from channels and starts the Zeebe workflows that drive the rest of Payment Hub.

[![License](https://img.shields.io/badge/License-MPL--2.0-blue.svg)](LICENSE)

## What it does
- Exposes REST APIs for callers and channels to send payment requests (transfers, transactions, collections, party registration, validation, and more).
- Turns each incoming request into a new Zeebe (Camunda) workflow instance that orchestrates the payment end to end.
- Uses Apache Camel routes to accept requests, map them, and hand them to the Zeebe engine.
- Picks the right workflow definition per tenant, so different deployments can run their own process.
- Includes a GSMA Mobile Money API surface for that standard's request formats.
- Serves an OpenAPI (Swagger) UI so the exposed APIs are easy to explore.

## How it fits into Payment Hub EE
This is the front door of Payment Hub. Other connectors (AMS, mojaloop, mpesa, and so on) wait for work handed to them by a running workflow. The channel connector is what actually starts those workflows: it takes a request from the outside, creates a Zeebe workflow instance, and from there the engine calls each connector in turn. Without it, nothing kicks off. It is the largest connector because it holds the API definitions and the routes that map many request types onto their workflows.

## Tech stack
- Java 21
- Spring Boot 3.4
- Apache Camel 4 (Camel Spring Boot, Jetty, HTTP)
- Zeebe / Camunda client (workflow orchestration)
- Redis (via Spring Data Redis)
- Gradle build
- Depends on `paymenthub-ee-bom` for versions and `paymenthub-ee-core` for shared connector code

## Branches
- `dev` is the active development branch — all PRs should target `dev`.
- `main` holds released versions.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) and our [Code of Conduct](CODE_OF_CONDUCT.md).
