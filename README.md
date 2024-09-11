# Cloudflare DNS Updater

This Spring Boot application is designed to run as a cron job and update DNS records on Cloudflare.
The service uses [ipify](https://www.ipify.org/) to poll for its public ip.

## Prerequisites

Before running the application, make sure you have the following:

- Cloudflare domain
- Cloudflare API key
- Cloudflare account email

Optional:

- Specific A records to update

## Run containerized

### Kubernetes with helm

Helm overrides are located under `/k8s`.

All magnuen2k charts can be found [here](https://gitlab.magnuen2k.com/homelab/charts)

### Docker

- Login to registry with your account
    ```shell
    docker login registry.magnuen2k.com
    ```

- Run from command line or with `docker-compose.yml`

1. Command line
    ```shell
    docker run \
      --name my-dns-updater \
      -e "poll.cron"="0 */1 * * * *" \
      -e "cloudflare.domain"="YOUR_CLOUDFLARE_DOMAIN" \
      -e "cloudflare.api.key"="YOUR_CLOUDFLARE_API_KEY" \
      -e "cloudflare.api.email"="YOUR_CLOUDFLARE_EMAIL" \
      registry.magnuen2k.com/homelab/util/dns-updater:latest
    ```
2. Docker compose
     ```yaml
    version: "2.2"
    services:
      dnsupdater:
        image: registry.magnuen2k.com/homelab/util/dns-updater:latest
        container_name: my-dns-updater
        ports:
          - "8080:8080"
        environment:
          - cloudflare.domain=YOUR_CLOUDFLARE_DOMAIN
          - cloudflare.api.key=YOUR_CLOUDFLARE_API_KEY
          - cloudflare.api.email=YOUR_CLOUDFLARE_EMAIL
          - cloudflare.records=some.record.com,another.one.com # THIS IS OPTIONAL
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
