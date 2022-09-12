(ns fenum.events
  (:require [fenum.db :as db]))

(defn login
  []
  (swap! db/state assoc :auth? true))

(defn logout
  []
  (swap! db/state assoc :auth? false))

(defn toggle-user-dropdown
  []
  (let [dropdown (:user-dropdown? @db/state)]
    (swap! db/state assoc :user-dropdown? (not dropdown))))

(defn load-sqlite-database
  [sqlite-db]
  (swap! db/state assoc :sqlite-db sqlite-db))
