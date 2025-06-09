# 🌍 Cloudflare DNS Updater

[![Build and Push Docker Image](https://github.com/magnuen2k/cloudflare-dns-updater/actions/workflows/build-push.yml/badge.svg)](https://github.com/magnuen2k/cloudflare-dns-updater/actions/workflows/build-push.yml)

This Spring Boot application is designed to run as a cron job and update DNS records on Cloudflare.
The service uses [ipify](https://www.ipify.org/) to poll for its public ip. You can configure the app to run for
multiple domains and zones, updating either all or specific records.

The container exposes port 8080 for optional health checks, but it is not used for any other purpose.

## Prerequisites

Before running the application, make sure you have the following:

- Cloudflare domain
- Cloudflare API key
- Cloudflare account email

Future features:

- Webhook notifications when IP changes

## 🚀 Getting started

### Docker

- Run from command line or with `docker-compose.yml`

1. Command line
    ```shell
    docker run \
      --name my-dns-updater \
      -e "poll.cron"="0 */1 * * * *" \
      -e "cloudflare.domains.DOMAIN1.domain"="YOUR_CLOUDFLARE_DOMAIN" \
      -e "cloudflare.domains.DOMAIN1.api-key"="YOUR_CLOUDFLARE_API_KEY" \
      -e "cloudflare.domains.DOMAIN1.api-email"="YOUR_CLOUDFLARE_EMAIL" \
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
          - cloudflare.domains.DOMAIN1.domain=YOUR_CLOUDFLARE_DOMAIN
          - cloudflare.domains.DOMAIN1.api-key=YOUR_CLOUDFLARE_API_KEY
          - cloudflare.domains.DOMAIN1.api-email=YOUR_CLOUDFLARE_EMAIL
          - poll.cron=0 */1 * * * *
      ```

## 📖 Configuration Reference

The application can be configured using environment variables or through the `application.yml` file.

### Root Properties

| Key                  | Type                       | Default                                | Description                                                                                                                                                                       |
|----------------------|----------------------------|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cloudflare.api`     | `String`                   | `https://api.cloudflare.com/client/v4` | Base URL for the Cloudflare API or integration endpoint.                                                                                                                          |
| `cloudflare.domains` | `Map<String, DomainConfig` | `emptyMap()`                           | A named map of domain configurations. Each key is a page ID (e.g. `some-page`). This is done to make it easier to inject configuration from docker-compose and not rely on lists. |

### `DomainConfig` Fields (under `cloudflare.domains.*`)

| Key         | Type           | Default       | Optional | Description                                                                             |
|-------------|----------------|---------------|----------|-----------------------------------------------------------------------------------------|
| `domain`    | `String`       | null          | false    | The actual domain name (e.g. `magnuen2k.com`).                                          |
| `api-key`   | `String`       | null          | false    | API key used to authenticate with Cloudflare.                                           |
| `api-email` | `String`       | null          | false    | Email associated with the API key.                                                      |
| `zones`     | `List<String>` | `emptyList()` | true     | Specific zone IDs. Default is to lookup all zones in a domain if none is specified.     |
| `records`   | `List<String>` | `emptyList()` | true     | Specific A-record. Default is to lookup all A-records in a domain if none is specified. |

---

### 🧪 Example Configuration (`application.yaml` or external config file)

```yaml
cloudflare:
  api-url: https://api.cloudflare.com/client/v4
  domains:
    WEBSITE-1:
      domain: YOUR_CLOUDFLARE_DOMAIN
      api-email: YOUR_CF_EMAIL
      api-key: YOUR_CF_API_KEY
    WEBSITE-2:
      domain: YOUR_CLOUDFLARE_DOMAIN
      api-email: YOUR_CF_EMAIL
      api-key: YOUR_CF_API_KEY

      # Optional: Specify zones and records to update
      zones:
        - ANY_ZONE_ID
        - ANY_ZONE_ID
      records:
        - ANY_RECORD_ID
        - ANY_RECORD_ID
```

Replace `YOUR_CLOUDFLARE_DOMAIN`, `YOUR_CLOUDFLARE_API_KEY` and `YOUR_CLOUDFLARE_EMAIL` with your actual Cloudflare API
credentials.

## Run locally

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
