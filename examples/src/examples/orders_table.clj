(ns examples.orders-table
  (:require [rjmetrics.core :as rjmetrics]))

(defn- sync-order
  [config order]
  (let [result (rjmetrics/push-data config
                                    "orders"
                                    (assoc order :keys ["id"]))]
    (if (= (-> result first :status) 201)
        (print "Synced order with id" (:id order) "\n")
        (print "Failed to sync order with id" (:id order) "\n"))))

(defn run
  []
  (let [config {:client-id 0 :api-key "your-api-key"}
        orders [{:id 1, :user_id 1 :value 58.40  :sku "milky-white-suede-shoes"}
                {:id 2, :user_id 1 :value 23.99  :sku "red-button-down-fleece"}
                {:id 3, :user_id 2 :value 5.00   :sku "bottle-o-bubbles"}
                {:id 4, :user_id 3 :value 120.01 :sku "zebra-striped-game-boy"}
                {:id 5, :user_id 5 :value 9.90   :sku "kitten-mittons"}]]
    (when (rjmetrics/authenticated? config)
      (dorun (map (partial sync-order config) users)))))
