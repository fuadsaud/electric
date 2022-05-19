(ns user.demo-system-properties
  (:require [clojure.string :as str]
            [hyperfiddle.photon :as p]
            [hyperfiddle.photon-dom :as dom]
            [hyperfiddle.ui :as ui])
  #?(:cljs (:require-macros user.demo-system-properties))
  (:import (hyperfiddle.photon Pending)))

(defn system-properties [?s]
  #?(:clj (->> (System/getProperties)
               (filter (fn [[k v]] (str/includes? (str/lower-case (str k)) (str/lower-case (str ?s)))))
               (into {}))))

(def !x #?(:clj (atom 10)))

(p/defn App []
  (dom/div
    (dom/h1 (dom/text "System Properties"))
    (let [filter (ui/Input. {} dom/target-value)]
      (dom/div (dom/text (str "Input: " filter)))
      (dom/table
        (dom/for [[k v] ~@(system-properties filter)]
          (dom/tr
            (dom/td (dom/text (pr-str k)))
            (dom/td (dom/text (pr-str v)))))))))

(def main #?(:cljs (p/client (p/main
                               (try
                                 (binding [dom/parent (dom/by-id "root")]
                                   (App.))
                                 (catch Pending _))))))

(comment
  (swap! !x dec)
  )
