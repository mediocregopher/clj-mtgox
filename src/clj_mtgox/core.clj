(ns clj-mtgox.core
    (:require [clj-http.client :as client]
              [clojure.data.codec.base64 :as b64]
              [cheshire.core :refer :all])
    (:import (javax.crypto Cipher KeyGenerator Mac SecretKeyFactory)
             (javax.crypto.spec IvParameterSpec SecretKeySpec)))

(defn- make-args [arg-vecs]
    (reduce #(let [[k v] %2]
                (str %1 k "=" v "&")) "" arg-vecs))

(defn- make-path
    [parts]
    (reduce #(str %1 \/ %2) (first parts) (rest parts)))

(defn- hmac-sha-512
    [secret msg]
    (let [hmac-key (SecretKeySpec. secret "HmacSHA512")
        hmac (doto (Mac/getInstance "HmacSHA512") (.init hmac-key))]
            (.doFinal hmac (.getBytes msg))))

(defn- make-rest-sign
    [secret path & post]
    (->> (if (empty? post) (str path \u0000) (str path \u0000 (first post)))
         (hmac-sha-512 secret)
         (b64/encode)
         (String.)))

(defn- nonce-vec [] ["nonce" (System/currentTimeMillis)])

(defn- process-response
    [res]
    (let [json-res (parse-string (res :body) true)]
        (if (= (json-res :result) "success") (json-res :data) json-res)))


;We use the default middleware, but without the lower-casing of header fields,
;because Mt.Gox doesn't follow the spec and expects Rest-Key/Sign to be
;capitalized
(def middleware
    (remove #(= client/wrap-lower-case-headers %) client/default-middleware))

(defn init
    "Given the string representation of the key and secret given on the MtGox site,
    returns a map which will be passed into other functions"
    [api-key api-secret]
    {:key      api-key
     :secret64 api-secret
     :secret   (b64/decode (.getBytes api-secret))})

(defn call
    "Given an object from init, a vector of url parts, and optionally key/val vectors
    for call arguments, returns the json decoded result from MtGox"
    [obj url-parts & arg-vecs]
    (let [full-arg-vecs (cons (nonce-vec) arg-vecs)
          post-args (make-args full-arg-vecs)
          path (make-path url-parts)
          url (str "https://data.mtgox.com/api/2/" path)
          rest-key (obj :key)
          rest-sign (make-rest-sign (obj :secret) path post-args)]
    (process-response
        (client/with-middleware middleware
            (client/post url
                {:body post-args
                 :content-type "application/x-www-form-urlencoded"
                 :headers { "Rest-Key"  rest-key
                            "Rest-Sign" rest-sign }})))))
