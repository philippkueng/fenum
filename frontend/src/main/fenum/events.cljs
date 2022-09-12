(ns fenum.events
  (:require [fenum.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::toggle-user-dropdown
  (fn [db _]
    (let [dropdown (:user-dropdown? db)]
      (assoc db :user-dropdown? (not dropdown)))))

#_(defn load-sqlite-database
    [sqlite-db]
    (swap! db/state assoc :sqlite-db sqlite-db))
