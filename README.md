# club-manager

Management software for small clubs like our http://www.loscaracoles.ch

Let's see where it leads. For now I'm using it to test some new stuff like event sourcing and springboot.

## Design Goals

- Components are event sourced
- Business modules can be used independently (e.g. person module may be used without member module)
- Business modules (identity, member, person) have no code dependencies between them (only dependencies to club-manager-commons* and club-manager-eventstore-iface modules are allowed)
- Apart from club-manager-springboot-app there must be no dependencies on springboot

## Design decisions

- For the time being the REST resource classes also implement the application services
- REST Resources use jax-rs. In the future it would be nice to allow for other implementations
- Authentication is done using JWT Bearer Tokens

## Setup

postgres:

https://help.ubuntu.com/community/PostgreSQL

sudo -u postgres psql postgres

\password postgres
<enter password>
Ctrl-d

sudo -u postgres createdb eventdb

## TODO

- allow deletion of subscriptions
- allow memberships (without subscriptions)
- allow comments for subscriptions

## Notes for developers

### extract public key from certificate pem:

openssl x509 -pubkey -noout -in cert.pem  > pubkey.pem