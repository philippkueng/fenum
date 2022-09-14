(ns fenum.subscriptions
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::user-dropdown?
  (fn [db]
    (:user-dropdown? db)))

(re-frame/reg-sub
  ::available-databases
  (fn [db] (:available-databases db)))

(re-frame/reg-sub
  ::selected-database
  (fn [db] (:selected-database db)))

(re-frame/reg-sub
  ::tables
  (fn [db] (:tables db)))

(re-frame/reg-sub
  ::selected-table
  (fn [db] (:selected-table db)))

(re-frame/reg-sub
  ::raw-database
  (fn [db]
    db))