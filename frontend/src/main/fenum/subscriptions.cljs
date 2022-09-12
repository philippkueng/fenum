(ns fenum.subscriptions
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::user-dropdown?
  (fn [db]
    (:user-dropdown? db)))

(re-frame/reg-sub
  ::raw-database
  (fn [db]
    db))