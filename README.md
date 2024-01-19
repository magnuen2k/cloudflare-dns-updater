# Cloudflare DNS Updater

This Spring Boot application is designed to run as a cron job and update DNS records on Cloudflare.

## Prerequisites

Before running the application, make sure you have the following:

- Cloudflare domain
- Cloudflare API key
- Cloudflare account email

## Run with Docker

- Login to registry with your account
    ```shell
    docker login artifactory.magnuen2k.com
    ```

- Run container from image (default latest tag)
    ```shell
    docker run \
      -n my-dns-updater \
      -e "poll.cron"="0 0 0 * * *" \
      -e "cloudflare.domain"="YOUR_CLOUDFLARE_DOMAIN" \
      -e "cloudflare.api.key"="YOUR_CLOUDFLARE_API_KEY" \
      -e "cloudflare.api.email"="YOUR_CLOUDFLARE_EMAIL" \
      artifactory.magnuen2k.com/homelab/util/dns-updater:latest
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
Replace `YOUR_CLOUDFLARE_DOMAIN`, `YOUR_CLOUDFLARE_API_KEY` and `YOUR_CLOUDFLARE_EMAIL` with your actual Cloudflare API credentials.

#### Cron Expression
Set the cron expression for the scheduled task in the application.yml file:

```yaml
poll:
  cron: "0 0 0 * * *"
```
This example schedules the task to run every day at midnight. Adjust the cron expression as needed.

## License
This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/license/mit/) file for details.
