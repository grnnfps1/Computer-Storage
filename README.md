# Computer Storage

A modular computer, storage, automation and energy mod for Minecraft Forge 1.20.1.

## Development

- Minecraft 1.20.1
- Forge 47.4.0
- Java 17

## Architecture

The project is built around an independent `Computer` instance. The motherboard is only a host; hardware and services are managed through dedicated managers.

## Foundation milestone

The first milestone establishes the Core, service container, event bus, hardware API, persistence layer and manager architecture before storage is implemented.

## License

MIT
