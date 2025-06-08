# Cloudflare DNS Updater

[![Build and Push Docker Image](https://github.com/magnuen2k/cloudflare-dns-updater/actions/workflows/build-push.yml/badge.svg)](https://github.com/magnuen2k/cloudflare-dns-updater/actions/workflows/build-push.yml)

This Spring Boot application is designed to run as a cron job and update DNS records on Cloudflare.
The service uses [ipify](https://www.ipify.org/) to poll for its public ip.

The container exposes port 8080 for optional health checks, but it is not used for any other purpose.

## Prerequisites

Before running the application, make sure you have the following:

- Cloudflare domain
- Cloudflare API key
- Cloudflare account email

Future features:

- Webhook notifications when IP changes
- Specific A records to update
- Support for multiple domains

## Run containerized

### Docker

- Run from command line or with `docker-compose.yml`

1. Command line
    ```shell
    docker run \
      --name my-dns-updater \
      -e "poll.cron"="0 */1 * * * *" \
      -e "cloudflare.domain"="YOUR_CLOUDFLARE_DOMAIN" \
      -e "cloudflare.api.key"="YOUR_CLOUDFLARE_API_KEY" \
      -e "cloudflare.api.email"="YOUR_CLOUDFLARE_EMAIL" \
      magnuen2k/cloudflare-dns-updater:latest
    ```
2. Docker compose
     ```yaml
    version: "2.2"
    services:
      dnsupdater:
        image: magnuen2k/cloudflare-dns-updater:latest
        container_name: my-dns-updater
        ports:
          - "8080:8080"
        environment:
          - cloudflare.domain=YOUR_CLOUDFLARE_DOMAIN
          - cloudflare.api.key=YOUR_CLOUDFLARE_API_KEY
          - cloudflare.api.email=YOUR_CLOUDFLARE_EMAIL
          - poll.cron=0 */1 * * * *
      ```

## Run locally

Adjust the application configuration in the `application.yml` file:

```yaml
cloudflare:
  domain: YOUR_CLOUDFLARE_DOMAIN
  api:
    url: https://api.cloudflare.com/client/v4
    key: YOUR_CLOUDFLARE_API_KEY
    email: YOUR_CLOUDFLARE_EMAIL
```

Replace `YOUR_CLOUDFLARE_DOMAIN`, `YOUR_CLOUDFLARE_API_KEY` and `YOUR_CLOUDFLARE_EMAIL` with your actual Cloudflare API
credentials.

#### Cron Expression

Set the cron expression for the scheduled task in the application.yml file:

```yaml
poll:
  cron: "0 */1 * * * *"
```

This example schedules the task to run every minute. Adjust the cron expression as needed.

## License

This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/license/mit/) file for
details.
