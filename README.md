# cms-server
The backend (server) which serves a REST API for clients to consume data for the course management system

## Environment Setup

### Github token to access packages
cms-server has dependencies
on many `spring` libraries *(maven dependencies)* as well as
a *shared* `core` library (developed as part of this project) which is used both by the
client module and the server module.

Since the shared `core` library is hosted as a Github Package,
you'll need to setup a Github token with your Github account for successful resolution of dependencies.

Navigate to `GitHub > Settings > Developer Settings > Personal access`
and click on  `Generate New Token`

Give **at-least** `read:packages` permission.

Copy the token and setup the following environment variables:

```zsh
export GITHUB_USERNAME={Your Github Username}
export GITHUB_TOKEN={Copied token from the above step}
```

Make sure that the names of environment variables are as it is.
Otherwise, you might face a tough time :P

### Code Style

This project uses [prettier-java](https://github.com/jhipster/prettier-java) to format JAVA code. 

To install the formatter, run the following:

```shell
make install-formatter
```

To format the code, run:

```shell
make fmt
```

You can customize the formatting rules by editing the `.prettierrc.yaml` file present in the project root.

### Running the server
One command is adequate to launch the server to start serving HTTP requests.

```shell
make
```
