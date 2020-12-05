# Philips Hue Configuration Guide

## Required values
- `internalIp`
    - the internal IP address of your Hue bridge found here: [Hue Broker Server](https://discovery.meethue.com/)
- `deviceName`
    - a unique name of the device you are running on, i.e. `lucas-pc`

## Optional values
- `token`
    - this is set automatically after the first successful startup
- `sleep` (default: 15s)
    - amount of time to sleep between each authentication retry
- `retries` (default: 5)
    - number of authentication retry