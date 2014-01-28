(ns rjmetrics.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def API-BASE "https://connect.rjmetrics.com/v2")
(def SANDBOX-BASE "https://sandbox-connect.rjmetrics.com/v2")

(def MAX-REQUEST-SIZE 100)

(defn- make-api-call
  [url data]
  (let [response (client/post url {:body (json/generate-string data)
                                 :content-type :json
                                 :throw-exceptions false})]
    {:status (:status response)
     :body (json/parse-string (:body response) true)}))

(defn push-data
  ([config table-name data]
   (push-data config table-name data API-BASE))
  ([config table-name data url-base]
   {:pre [(contains? config :api-key)
          (contains? config :client-id)
          (string? (:api-key config))
          (pos? (:client-id config))]}
   (let [{:keys [client-id api-key]} config
         url (str url-base "/client/" client-id "/table/" table-name "/data?apikey=" api-key)
         call-url (partial make-api-call url)]
     (if (sequential? data)
       (map call-url (partition-all MAX-REQUEST-SIZE data))
       (list (make-api-call url data))))))

(defn authenticated?
  [config]
  (let [test-data {:keys ["id"]
                   :id 1}
        response (first (push-data config "test" test-data SANDBOX-BASE))]
    (= 201 (:status response))))
