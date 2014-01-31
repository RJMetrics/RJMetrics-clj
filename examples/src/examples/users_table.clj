(ns examples.users-table
  (:require [rjmetrics.core :as rjmetrics]))

(defn- sync-user
  [config user]
  (let [result (rjmetrics/push-data config
                                    ;; table named "users"
                                    "users"
                                    ;; user_id is the unique key here, since each user
                                    ;; should only have one record in the table
                                    (assoc user :keys ["id"]))]
    (if (= (-> result first :status) 201)
        (print "Synced user with id" (:id user) "\n")
        (print "Failed to sync user with id" (:id user) "\n"))))

(defn run
  []
  (let [config {:client-id 0 :api-key "your-api-key"}
        ;; let's define some fake users
        users [{:id 1, :email "joe@schmo.com", :acquisition_source "PPC"}
               {:id 2, :email "mike@smith.com", :acquisition_source "PPC"}
               {:id 3, :email "lorem@ipsum.com", :acquisition_source "Referral"}
               {:id 4, :email "george@vandelay.com", :acquisition_source "Organic"}
               {:id 5, :email "larry@google.com", :acquisition_source "Organic"}]]
    ;; make sure the client is authenticated before we do anything
    (when (rjmetrics/authenticated? config)
      ;; iterate through users and push data
      (dorun (map (partial sync-user config) users)))))
