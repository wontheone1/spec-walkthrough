(ns spec-walkthrough.sampling-generators
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.gen.alpha :as gen]))

(gen/generate (s/gen int?))
;;=> -959
(gen/generate (s/gen nil?))
;;=> nil
(gen/sample (s/gen string?))
;;=> ("" "" "" "" "8" "W" "" "G74SmCm" "K9sL9" "82vC")
(gen/sample (s/gen #{:club :diamond :heart :spade}))
;;=> (:heart :diamond :heart :heart :heart :diamond :spade :spade :spade :club)

(gen/sample (s/gen (s/cat :k keyword? :ns (s/+ number?))))
;;=> ((:D -2.0)
;;=>  (:q4/c 0.75 -1)
;;=>  (:*!3/? 0)
;;=>  (:+k_?.p*K.*o!d/*V -3)
;;=>  (:i -1 -1 0.5 -0.5 -4)
;;=>  (:?!/! 0.515625 -15 -8 0.5 0 0.75)
;;=>  (:vv_z2.A??!377.+z1*gR.D9+G.l9+.t9/L34p -1.4375 -29 0.75 -1.25)
;;=>  (:-.!pm8bS_+.Z2qB5cd.p.JI0?_2m.S8l.a_Xtu/+OM_34* -2.3125)
;;=>  (:Ci 6.0 -30 -3 1.0)
;;=>  (:s?cw*8.t+G.OS.xh_z2!.cF-b!PAQ_.E98H4_4lSo/?_m0T*7i 4.4375 -3.5 6.0 108 0.33203125 2 8 -0.517578125 -4))