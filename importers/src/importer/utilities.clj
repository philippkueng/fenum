(ns importer.utilities)

;; from https://andersmurphy.com/2019/11/30/clojure-flattening-key-paths.html
(defn flatten-paths
  ([m separator]
   (flatten-paths m separator []))
  ([m separator path]
   (->> (map (fn [[k v]]
               (if (and (map? v) (not-empty v))
                 (flatten-paths v separator (conj path k))
                 [(->> (conj path k)
                    (map name)
                    (clojure.string/join separator)
                    keyword) v]))
          m)
     (into {}))))

;; from https://stackoverflow.com/questions/43722091/clojure-programmatically-namespace-map-keys
(defn map->nsmap
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                  (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k) ]
                 (assoc acc new-kw v)))
    {} m))