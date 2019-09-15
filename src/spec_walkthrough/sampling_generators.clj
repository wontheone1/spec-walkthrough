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

;; Generating a game of cards

(def suit? #{:club :diamond :heart :spade})
(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def deck (for [suit suit? rank rank?] [rank suit]))

(s/def ::card (s/tuple rank? suit?))
(s/def ::hand (s/* ::card))

(s/def ::name string?)
(s/def ::score int?)
(s/def ::player (s/keys :req [::name ::score ::hand]))

(s/def ::players (s/* ::player))
(s/def ::deck (s/* ::card))
(s/def ::game (s/keys :req [::players ::deck]))

(gen/generate (s/gen ::player))
;;=> {:spec.examples.guide/name "sAt8r6t",
;;    :spec.examples.guide/score 233843,
;;    :spec.examples.guide/hand ([8 :spade] [5 :heart] [9 :club] [3 :heart])}

(gen/generate (s/gen ::game))

;;
;; Exercise
;;

(s/exercise (s/cat :k keyword? :ns (s/+ number?)) 5)
;;=>
;;([(:y -2.0) {:k :y, :ns [-2.0]}]
;; [(:_/? -1.0 0.5) {:k :_/?, :ns [-1.0 0.5]}]
;; [(:-B 0 3.0) {:k :-B, :ns [0 3.0]}]
;; [(:-!.gD*/W+ -3 3.0 3.75) {:k :-!.gD*/W+, :ns [-3 3.0 3.75]}]
;; [(:_Y*+._?q-H/-3* 0 1.25 1.5) {:k :_Y*+._?q-H/-3*, :ns [0 1.25 1.5]}])

(s/exercise (s/or :k keyword? :s string? :n number?) 5)
;;=> ([:H [:k :H]]
;;    [:ka [:k :ka]]
;;    [-1 [:n -1]]
;;    ["" [:s ""]]
;;    [-3.0 [:n -3.0]])

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

(s/exercise-fn `ranged-rand)

;=>
;([(-2 -1)   -2]
; [(-3 3)     0]
; [(0 1)      0]
; [(-8 -7)   -8]
; [(3 13)     7]
; [(-1 0)    -1]
; [(-69 99) -41]
; [(-19 -1)  -5]
; [(-1 1)    -1]
; [(0 65)     7])

;;
;; Using s/and Generators
;;

(gen/generate (s/gen even?))
;; Execution error (ExceptionInfo) at user/eval1281 (REPL:1).
;; Unable to construct gen at: [] for: clojure.core$even_QMARK_@73ab3aac

(gen/generate (s/gen (s/and int? even?)))
;;=> -15161796

(defn divisible-by [n] #(zero? (mod % n)))

(gen/sample (s/gen (s/and int?
                          #(> % 0)
                          (divisible-by 3))))
;;=> (3 9 1524 3 1836 6 3 3 927 15027)

(gen/sample (s/gen (s/and pos-int? (divisible-by 3))))
;;=> (3 3 15 6 6 3 12 531 195 3)

;; hello, are you the one I'm looking for?
(gen/sample (s/gen (s/and string? #(clojure.string/includes? % "hello"))))
;; Error printing return value (ExceptionInfo) at clojure.test.check.generators/such-that-helper (generators.cljc:320).
;; Couldn't satisfy such-that predicate after 100 tries.

;;
;; Custom Generators
;;

(s/def ::kws (s/and keyword? #(= (namespace %) "my.domain")))
(s/valid? ::kws :my.domain/name) ;; true
(gen/sample (s/gen ::kws)) ;; unlikely we'll generate useful keywords this way

(def kw-gen (s/gen #{:my.domain/name :my.domain/occupation :my.domain/id}))
(gen/sample kw-gen 5)
;;=> (:my.domain/occupation :my.domain/occupation :my.domain/name :my.domain/id :my.domain/name)

(s/def ::kws (s/with-gen (s/and keyword? #(= (namespace %) "my.domain"))
                         #(s/gen #{:my.domain/name :my.domain/occupation :my.domain/id})))
(s/valid? ::kws :my.domain/name)  ;; true
(gen/sample (s/gen ::kws))
;;=> (:my.domain/occupation :my.domain/occupation :my.domain/name  ...)

(def kw-gen-2 (gen/fmap #(keyword "my.domain" %) (gen/string-alphanumeric)))
(gen/sample kw-gen-2 5)
;;=> (:my.domain/ :my.domain/ :my.domain/1 :my.domain/1O :my.domain/l9p2)

(def kw-gen-3 (gen/fmap #(keyword "my.domain" %)
                        (gen/such-that #(not= % "")
                                       (gen/string-alphanumeric))))
(gen/sample kw-gen-3 5)
;;=> (:my.domain/O :my.domain/b :my.domain/ZH :my.domain/31 :my.domain/U)

(s/def ::hello
  (s/with-gen #(clojure.string/includes? % "hello")
              #(gen/fmap (fn [[s1 s2]] (str s1 "hello" s2))
                         (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))))
(gen/sample (s/gen ::hello))
;;=> ("hello" "ehello3" "eShelloO1" "vhello31p" "hello" "1Xhellow" "S5bhello" "aRejhellorAJ7Yj" "3hellowPMDOgv7" "UhelloIx9E")
