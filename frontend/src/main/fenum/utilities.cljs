(ns fenum.utilities)

(defn value [element]
  (-> element .-target .-value))

(defn get-element-by-id [id]
  (.getElementById js/document id))
