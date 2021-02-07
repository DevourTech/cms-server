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

### MYSQL setup
To set up a mysql instance on your local, follow these steps:

* Download mysql server and follow the secure installation steps from [here](https://linuxhint.com/install_mysql_ubuntu_2004/)
* Create a user (using the above link) which will be used to authenticate and perform CRUD operations on the db
* Grant privileges to the user (refer above link).
* Create the db to perform CRUD operations.
* Create the following environment variables:
```shell
export CMS_MYSQL_HOST=localhost
export CMS_MYSQL_PORT=3306
export CMS_MYSQL_USERNAME=<Name of the user created above>
export CMS_MYSQL_PASSWORD=<Password for the above user>
export CMS_MYSQL_DB=<Name of the database to perform CRUD operations>
```

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
