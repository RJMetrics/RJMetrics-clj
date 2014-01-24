(ns rjmetrics-clj.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def API-BASE "https://connect.rjmetrics.com/v2")
(def SANDBOX-BASE "https://sandbox-connect.rjmetrics.com/v2")

(defn- result-is-success?
  [result]
  (true? (some #(= (:status result) %) [200 201])))

(defn make-api-call
  [url data]
  (client/post url
               {:body (json/generate-string data)
                :content-type :json}))

(defn push-data
  ([config table-name data]
   (push-data config table-name data API-BASE))
  ([config table-name data url-base]
   (let [{:keys [client-id api-key]} config
         url (str url-base "/client/" client-id "/table/" table-name "/data?apikey=" api-key)
         call-url (partial make-api-call url)]
     (if (sequential? data)
       (map #(-> %
                 call-url
                 result-is-success?)
            (partition-all 100 data))
       (list (result-is-success? (make-api-call url data)))))))

(defn authenticated?
  [config]
  (let [test-data {:keys ["id"]
                   :id 1}]
    (push-data config "test" test-data SANDBOX-BASE)))
