# MicroOauth2Server
Fast, Lightweight and highly customizable Oauth2 Server

## General
MicroOauth2Server implements oauth2 server functionality defined in https://tools.ietf.org/html/rfc6749 \
The project aims to offer lightweight and performant core framework which can be easily expanded with plugins to 
match different environments. Every layer is abstracted and can be replaced with minimal effort.

Please note that the project is currently in very early development and master branch is not guaranteed 
to be stable yet as most features are not ready. When the core features have been implemented 
then release versions will be created and a proper release cycle will be followed.

## Token cache

MicroOauth abstracts cache controller and the underlying storage can be changed in configuration file 
to use pre-existing controller. In specific situations when customization is required, a fully custom 
plugin for token cache controller can be implemented.
Even though SQL databases for token cache are supported, I would encourage to use Redis as it will 
be more performant and removes the need to manage cache evictions in MicroOauth.

## Client storage

MicroOauth abstracts client storage and the underlying storage can be changed in configuration file 
to use pre-existing controller. In specific situations when customization is required, a fully custom 
plugin for token cache controller can be implemented.

## Tokens

MicroOauth will support different types of authorization tokens including standard token and JWT. 
Standard tokens are randomly generated tokens which are encrypted with AES-128-GCM. This asserts 
token integrity and makes timing attacks impractical, as well as allows invalid tokens to be rejected 
faster without having to query token cache.

## How to build
MicroOauth targets JDK 11 and comes with gradle wrapper. To build the project: \
```
git clone https://github.com/etsubu/MicroOauth2Server.git
./gradlew clean build
```
This will be build the application in ./build/libs


## Plugins

Instructions on how to create plugins and replace components will be defined here when the project 
is mature enough.