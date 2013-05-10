# clj-mtgox

An UNOFFICIAL clojure library for interacting with MtGox's api/2 endpoint.

## Disclaimer

The documentation for MtGox's api is sketchy at the moment, most of my information on its current
state I got from [this](https://bitbucket.org/nitrous/mtgox-api/overview) page (super thank you to
nitrous for putting that together). I've made this library fairly low level; it pretty much just
handles the api-key signing stuff, the rest is up to you. It is still possible, however,
that the API could change and this becomes out of date, so test before using!

## Installation

```
[org.clojars.mediocregopher/clj-mtgox "0.3"]
```

## Usage

First get an api key/secret pair from MtGox. Once logged in go to Security -> Advanced API Key
Creation, create a named pair, and save both the key and the secret, as they won't be shown to you
again.

In your clojure code you need to create an initial goxmap:
```clojure
(require '[clj-mtgox.core :as gox])

(def g (gox/init api-key api-secret))
```

To actually perform a call you use the `call` method:
```clojure
(gox/call g ["BTCUSD" "money" "info"])
```

The response is json-decoded and, if `(= (json-res :result) "success")` the `data` field will be
returned. The whole json-decoded response is returned on an error.

To pass in arguments just add more vectors:
```clojure
(gox/call g ["BTCUSD" "money" "order" "add"] ["type" "ask"] ["amount_int" "9001"])
```

## License

Copyright © 2013

Distributed under the Eclipse Public License, the same as Clojure.
