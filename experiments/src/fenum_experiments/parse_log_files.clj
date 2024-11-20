(ns fenum-experiments.parse-log-files
  (:require [instaparse.core :as insta])
  (:import (java.time Instant LocalDateTime ZoneOffset)
           (java.time.format DateTimeFormatter)))

(def log-parser
  (insta/parser
    "log = date <space> level <space> namespace <space> actor <space> rest
     date = #'\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}'
     level = #'WARN|INFO|ERROR|DEBUG|TRACE'
     namespace = <'['> #'[^\\]]+' <']'>
     actor = <'['> #'[^\\]]*' <']'>
     space = #'\\s+'
     rest = #'.*'"))


(def date-time-formatter
  (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSS"))

(defn parse-date [date-str]
  (-> (LocalDateTime/parse date-str date-time-formatter)
    (.atZone (ZoneOffset/UTC))
    .toInstant))

(def parse-log (partial insta/transform
                 {:date parse-date
                  :level str
                  :namespace str
                  :actor str
                  :rest str
                  :log (fn [date level namespace actor rest]
                         {:date date
                          :level level
                          :namespace namespace
                          :actor (if (empty? actor) nil actor)
                          :message rest})}))

(defn parse-log-line [log-line]
  (let [parsed (parse-log (log-parser log-line))]
    (assoc parsed :original-line log-line)))

;; Example usage
(def example-log "2024-11-11T11:00:00.624 WARN  [t.w.s.scheduler.SchedulerService$] [] - The following validators could not be found by their public key: List()")

(println (parse-log-line example-log))


(comment

  (let [log-lines (list
                    "2024-11-11T11:00:00.624 WARN  [t.w.s.scheduler.SchedulerService$] [] - The following validators could not be found by their public key: List()"
                    "2024-11-11T11:00:00.718 INFO  [t.w.s.scheduler.SchedulerService$] [ng/SchedulerService/384/b7483c9a-1d12-499d-98e0-64a519ebb1a0] - ValidatorCommand with messageId: b254ecd2-d2ce-4249-af63-1e74baa1ed45 Accepted")]
    (->> log-lines
      (map #(parse-log-line %))))

  )
