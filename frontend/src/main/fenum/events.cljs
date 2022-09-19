(ns fenum.events
  (:require [fenum.db :as db]
            [fenum.sqlite-db-utils :as sqlite-db-utils]
            [re-frame.core :as re-frame]
            ["tauri-plugin-sql-api$default" :as Database]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::toggle-user-dropdown
  (fn [db _]
    (let [dropdown (:user-dropdown? db)]
      (assoc db :user-dropdown? (not dropdown)))))

(re-frame/reg-event-fx
  ::load-database
  (fn [{:keys [db]} [_ database]]
    {:db (assoc db :selected-database database)
     :fx [[:dispatch [::toggle-user-dropdown]]
          [::load-database-fx database]]}))

(re-frame/reg-event-fx
  ::set-database-connection
  (fn [{:keys [db]} [_ connection]]
    {:db (assoc db :database-connection connection)
     :fx [[::read-tables-fx connection]]
     }))

(re-frame/reg-event-db
  ::set-available-tables
  (fn [db [_ tables]]
    (assoc db :tables tables)))

(re-frame/reg-fx
  ::load-database-fx
  (fn [database]
    (.then (.load Database (str "sqlite:" (:path database)))
      (fn [connection]
        (re-frame/dispatch [::set-database-connection connection])))))

(re-frame/reg-fx
  ::read-tables-fx
  (fn [connection]
    (.then (.select connection "SELECT name FROM sqlite_schema")
      (fn [tables]
        (re-frame/dispatch [::set-available-tables (js->clj tables :keywordize-keys true)])))))

(re-frame/reg-event-db
  ::set-available-fields
  (fn [db [_ fields]]
    (assoc db :available-fields fields)))

;; query: PRAGMA table_info('tweet');
(re-frame/reg-fx
  ::read-fields-fx
  (fn [[connection table]]
    ;; As we can't currently evaluate PRAGMA queries we're fetching the first table record and deduct the fields
    ;;  and types from that.
    (let [query (str "select * from " (:name table) " limit 1")]
      (js/console.log query)
      (.then (.select connection query)
        (fn [fields]
          (re-frame/dispatch [::set-available-fields (->> (js->clj fields :keywordize-keys true)
                                                       first
                                                       (sqlite-db-utils/row->fields-and-types))]))))))

(re-frame/reg-event-fx
  ::load-table
  (fn [{:keys [db]} [_ table]]
    {:db (assoc db :selected-table table)
     :fx [[::read-fields-fx [(:database-connection db) table]]
          [::select-rows-fx [(:database-connection db) table]]]}))

(re-frame/reg-event-db
  ::set-rows
  (fn [db [_ rows]]
    (assoc db :rows rows)))

(re-frame/reg-fx
  ::select-rows-fx
  (fn [[connection table]]
    (let [query (str "select * from " (:name table) " limit 10")]
      (.then (.select connection query)
        (fn [rows]
          (re-frame/dispatch [::set-rows (js->clj rows :keywordize-keys true)]))))))