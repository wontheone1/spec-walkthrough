(ns spec-walkthrough.explain
  (:require [clojure.spec.alpha :as s]))

(s/explain ::suit 42)
;; 42 - failed: #{:spade :heart :diamond :club} spec: :user/suit
(s/explain ::big-even 5)
;; 5 - failed: even? spec: :user/big-even
(s/explain ::name-or-id :foo)
;; :foo - failed: string? at: [:name] spec: :user/name-or-id
;; :foo - failed: int? at: [:id] spec: :user/name-or-id

(s/explain-data ::name-or-id :foo)
;;=> #:clojure.spec.alpha{
;;     :problems ({:path [:name],
;;                 :pred string?,
;;                 :val :foo,
;;                 :via [:spec.examples.guide/name-or-id],
;;                 :in []}
;;                {:path [:id],
;;                 :pred int?,
;;                 :val :foo,
;;                 :via [:spec.examples.guide/name-or-id],
;;                 :in []})}

(s/explain-str ::name-or-id :foo)
;":foo - failed: string? at: [:name] spec: :spec-walkthrough.core/name-or-id
; :foo - failed: int? at: [:id] spec: :spec-walkthrough.core/name-or-id
; "
