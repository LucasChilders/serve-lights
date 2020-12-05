# serve-lights

This project provides a single interface to control multiple providers of smart lights. This enables you to easily 
write complex home automation with a single light control implementation. The list of currently supported lights can 
be found [below](#provider-support). 


## Setup

This project should run on a machine on your local network. Run the `serve-lights.jar` executable after configuring each 
provider.

## Configure

Set an environment variable `SERVE_LIGHT_CONFIG_DIR` with the path to a directory where your configuration files are 
stored. Configuration files are named `<provider>.yaml`. The provider is the lower-cased simple class name, i.e. Hue.java -> 
`hue.lifx`. Each provider has its own unique configurations, check each `README-<provider>.md` linked below under 
[Provider Support](#provider-support)

<a name="provider-support"></a>
## Provider Support

### feature-complete
- Hue
    - [config](README-hue.md)

## todo

```diff
- LIFX
- Nanoleaf
```