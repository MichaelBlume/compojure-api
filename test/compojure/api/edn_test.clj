(ns compojure.api.edn-test
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [ring.util.http-response :refer [ok]]
            [midje.sweet :refer :all]
            [compojure.api.edn :refer :all])
  (:import [java.io ByteArrayInputStream]))

(defn stream [s]
  (-> s (.getBytes "utf-8") (ByteArrayInputStream.) (io/reader) (java.io.PushbackReader.)))

(defn edn-request [s]
  {:content-type "application/edn"
   :body (stream s)
   :headers {"accept" "application/edn"}})

(def with-edn-support (edn-support identity))

(fact edn-request?
  (edn-request? {:content-type "application/xml"})            => false
  (edn-request? {:content-type "application/json"})           => false
  (edn-request? {:content-type "application/vnd.myapp+json"}) => false
  (edn-request? {:content-type "application/ednz"})           => false
  (edn-request? {:content-type "application/edn"})            => true
  (edn-request? {:content-type "application/vnd.myapp+edn"})  => true)

(fact edn-support

   (fact edn-request-support

     (fact edn-list
       (let [request (with-edn-support (edn-request "[1 2 3]"))]
         (:body request)         => "[1 2 3]"
         (:body-params request)  => [1 2 3]
         (:edn-params request)   => [1 2 3]
         (:json-params request)  => nil
         (:params request)       => nil))

     (fact edn-map
       (let [request (with-edn-support (edn-request "{\"a\" 1 :b \"value\"}"))]
         (:body request)         => "{\"a\" 1, :b \"value\"}"
         (:body-params request)  => {"a" 1 :b "value"}
         (:edn-params request)   => {"a" 1 :b "value"}
         (:params request)       => {"a" 1 :b "value"}))

     (fact edn-primitive
       (let [request (with-edn-support (edn-request "true"))]
         (:body request)         => "true"
         (:body-params request)  => true
         (:edn-params request)   => true
         (:params request)       => nil))

     (fact edn-capability
       (let [request (with-edn-support (edn-request "true" ))]
         (-> request :meta :consumes) => ["application/edn"]
         (-> request :meta :produces) => ["application/edn"]))

     (fact edn-opts
       (let [edn-opts {:edn-opts {:readers {'uc s/upper-case}}}]
         (-> (edn-request "#uc foo")
             ((edn-support identity edn-opts))
             :edn-params)
         => "FOO"))))