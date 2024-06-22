<img src="assets/logo.png" align="left">
<h1>DiscordSRVOAuth</h1>
Allow players to link their Discord account via Discord OAuth2
<a href="https://discord.gg/7FDdBCbAcq"><img height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-singular_vector.svg"></a>

<br />

<div align="center">
<a href="https://modrinth.com/plugin/discordsrvoauth"><img height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<a href="https://hangar.papermc.io/PadowYT2/DiscordSRVOAuth"><img height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg"></a>
<a href="https://github.com/PadowYT2/DiscordSRVOAuth/releases"><img height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
</div>

## Usage

1. Download the plugin
2. Upload the plugin to your Minecraft Server (make sure it has DiscordSRV)
3. Make sure the port is open and not used
4. Make sure the URL is valid

### Setting up Discord values

Everything is being done within Discord application's settings > OAuth2

#### Values

Copy over the Client ID & Client Secret to the config. Then you would need to configure Redirects. It must be what you have in the config. An example is shown below

![](/assets/redirects.png)

### Setting up HTTPS

> [!NOTE]  
> Setting this up is optional, but recommended if you have a large player base

> [!TIP]
> Having HTTPS removes the port from the URL

Some hosts already have this feature, search for "Reverse Proxy". Otherwise you would need to have a VPS/VDS or a Dedicated Server to do this. This tutorial is made for NGINX and assumes you have it installed

1. Install certbot via `apt install -y certbot python3-certbot-nginx`
2. Make sure your domain points to the machine's IP
3. Generate the certificate via `certbot certonly --nginx -d example.com`
4. Create a new file `/etc/nginx/sites-enabled/discordsrvoauth.conf`
5. Paste in the following replacing `<domain>` with your domain and `<port>` with the port you are using
```nginx
server {
  listen 80;
  server_name <domain>;
  return 301 https://$host$request_uri;
}

server {
  listen 443 ssl;
  server_name <domain>;
  ssl_certificate /etc/letsencrypt/live/<domain>/fullchain.pem;
  ssl_certificate_key /etc/letsencrypt/live/<domain>/privkey.pem;

  location / {
    proxy_pass http://localhost:<port>/;

    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "Upgrade";
  }
}
```
6. Reload NGINX via `nginx -s reload`

And should be good to go, if you have any troubles make sure to join my Discord Server
