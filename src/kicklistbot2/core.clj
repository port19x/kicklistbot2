(ns kicklistbot2.core
  (:require [cheshire.core :refer :all])
  (:require [clj-http.client :as client]))

(def don
    (->> (:body (client/get "https://api.clashroyale.com/v1/clans/%23JGLJR8L/members" {:headers {:Authorization (str "Bearer " (slurp "token"))}}))
         (#(decode % true))
         (:items)
         (map #(map % [:name :role :donations]))
    ))

(def war
   (->> (:body (client/get "https://api.clashroyale.com/v1/clans/%23JGLJR8L/currentriverrace" {:headers {:Authorization (str "Bearer " (slurp "token"))}}))
        (#(decode % true))
        (:clan)
        (:participants)
        (map #(map % [:name :decksUsed]))
   ))

(def dondic (sort-by :name (map #(hash-map :name (first %) :role (second %) :donations (last %)) don)))
(def wardic (sort-by :name (map #(hash-map :name (first %) :decks (second %)) war)))
(def finally (group-by :name (concat dondic wardic)))
(def forge (map #(merge-with (fn [ra osiris] (max ra osiris)) % {:donations 0, :decks 0}) (map #(merge (first (second %)) (second (second %))) finally)))

(println (map :name (filter #(and (= "member" (:role %)) (> 100 (:donations %)) (> 8 (:decks %))) forge)))
(println (map :name (filter #(and (= "member" (:role %)) (< 300 (:donations %)) (<= 8  (:decks %))) forge)))
