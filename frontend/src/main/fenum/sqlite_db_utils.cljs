(ns fenum.sqlite-db-utils)

(defn row->fields-and-types [row]
  (->> row
    (map (fn [[key val]]
           {key (cond
                  (string? val) "string"
                  (number? val) "number"
                  :else "unknown")}))
    (apply merge)))

(comment
  (let [row {:author_id "10255262",
             :created_at "2022-09-01T23:49:46.000Z",
             :id 1,
             :lang "en",
             :text
             "A lot of managers, especially those from an engineering background, think that management is about doing stuff: defining rules, policies, and procedures; assigning tasks; creating external incentives; fixing problems.",
             :tweet_id "1565487105907703808"}]
    (->> row
      (map (fn [[key val]]
             {key (cond
                    (string? val) "string"
                    (number? val) "number"
                    :else "unknown")}))
      (apply merge)))

  (string? "something")
  (number? "something")
  (number? 2)
  )
