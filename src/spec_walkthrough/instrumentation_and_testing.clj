(ns spec-walkthrough.instrumentation-and-testing
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as stest]))

; Instrumentation

(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

(s/fdef ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

(ranged-rand 8 5)

; (stest/instrument `ranged-rand)

; (ranged-rand 8 5)
;Execution error - invalid arguments to user/ranged-rand at (REPL:1).
;{:start 8, :end 5} - failed: (< (:start %) (:end %))

; Testing

(stest/check `ranged-rand)
;;=> ({:spec #object[clojure.spec.alpha$fspec_impl$reify__13728 ...],
;;     :clojure.spec.test.check/ret {:result true, :num-tests 1000, :seed 1466805740290},
;;     :sym spec.examples.guide/ranged-rand,
;;     :result true})

(defn broken-ranged-rand  ;; BROKEN!
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- start end)))))

(s/fdef broken-ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

(stest/abbrev-result (first (stest/check `broken-ranged-rand)))

; To test all of the spec’ed functions in a namespace (or multiple namespaces), use enumerate-namespace to generate the set of symbols naming vars in the namespace:

(-> (stest/enumerate-namespace 'user) stest/check)

;;
;; Combining check and instrument
;;

;; code under test

(defn invoke-service [service request]
  ;; invokes remote service
  )

(defn run-query [service query]
  (let [{::keys [result error]} (invoke-service service {::query query})]
    (or result error)))

; We can spec these functions using the following specs:

(s/def ::query string?)
(s/def ::request (s/keys :req [::query]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result])
                        :err (s/keys :req [::error])))

(s/fdef invoke-service
        :args (s/cat :service any? :request ::request)
        :ret ::response)

(s/fdef run-query
        :args (s/cat :service any? :query string?)
        :ret (s/or :ok ::result :err ::error))

(stest/instrument `invoke-service {:stub #{`invoke-service}})
;;=> [spec.examples.guide/invoke-service]
(invoke-service nil {::query "test"})
;;=> #:spec.examples.guide{:error -11}
(invoke-service nil {::query "test"})
;;=> #:spec.examples.guide{:result ["kq0H4yv08pLl4QkVH8" "in6gH64gI0ARefv3k9Z5Fi23720gc"]}
(stest/summarize-results (stest/check `run-query))
;;=> {:total 1, :check-passed 1}
