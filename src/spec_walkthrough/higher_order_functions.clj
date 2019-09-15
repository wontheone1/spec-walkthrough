(ns spec-walkthrough.higher-order-functions
  (:require
    [clojure.spec.alpha :as s]))

(defn adder [x] #(+ x %))

(s/fdef adder
        :args (s/cat :x number?)
        :ret (s/fspec :args (s/cat :y number?)
                      :ret number?)
        :fn #(= (-> % :args :x) ((:ret %) 0)))
