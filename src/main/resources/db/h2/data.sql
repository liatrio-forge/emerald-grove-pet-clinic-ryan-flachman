INSERT INTO vets VALUES (default, 'James', 'Carter');
INSERT INTO vets VALUES (default, 'Helen', 'Leary');
INSERT INTO vets VALUES (default, 'Linda', 'Douglas');
INSERT INTO vets VALUES (default, 'Rafael', 'Ortega');
INSERT INTO vets VALUES (default, 'Henry', 'Stevens');
INSERT INTO vets VALUES (default, 'Sharon', 'Jenkins');

INSERT INTO specialties VALUES (default, 'radiology');
INSERT INTO specialties VALUES (default, 'surgery');
INSERT INTO specialties VALUES (default, 'dentistry');

INSERT INTO vet_specialties VALUES (2, 1);
INSERT INTO vet_specialties VALUES (3, 2);
INSERT INTO vet_specialties VALUES (3, 3);
INSERT INTO vet_specialties VALUES (4, 2);
INSERT INTO vet_specialties VALUES (5, 1);

INSERT INTO types VALUES (default, 'cat');
INSERT INTO types VALUES (default, 'dog');
INSERT INTO types VALUES (default, 'lizard');
INSERT INTO types VALUES (default, 'snake');
INSERT INTO types VALUES (default, 'bird');
INSERT INTO types VALUES (default, 'hamster');

INSERT INTO owners VALUES (default, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owners VALUES (default, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owners VALUES (default, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owners VALUES (default, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owners VALUES (default, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owners VALUES (default, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owners VALUES (default, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owners VALUES (default, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owners VALUES (default, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owners VALUES (default, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT INTO pets VALUES (default, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pets VALUES (default, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pets VALUES (default, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pets VALUES (default, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pets VALUES (default, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pets VALUES (default, 'George', '2010-01-20', 4, 5);
INSERT INTO pets VALUES (default, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 'Max', '2012-09-04', 1, 6);
INSERT INTO pets VALUES (default, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pets VALUES (default, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pets VALUES (default, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pets VALUES (default, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pets VALUES (default, 'Sly', '2012-06-08', 1, 10);

INSERT INTO visits (id, pet_id, visit_date, description) VALUES (default, 7, '2013-01-01', 'rabies shot');
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (default, 8, '2013-01-02', 'rabies shot');
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (default, 8, '2013-01-03', 'neutered');
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (default, 7, '2013-01-04', 'spayed');
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (default, 8, '2026-05-13', 'Owner reports Max has been lethargic for 3 days with markedly decreased appetite and increased water consumption (polydipsia). Physical exam: weight 4.1 kg (down 0.6 kg from last visit 6 months ago), mild dehydration estimated at 5% (skin tent ~2 sec), HR 168 bpm, temp 102.9°F, mm pale pink, CRT 2 sec. Abdominal palpation revealed mild cranial right quadrant discomfort and bilaterally small, irregular kidneys. Coat dull and unkempt. Urinalysis: SG 1.014, 2+ proteinuria, no glucosuria, no casts. CBC/Chemistry: BUN 52 mg/dL (ref 16-36), creatinine 2.8 mg/dL (ref 0.8-2.4), phosphorus 7.1 mg/dL (elevated), potassium 3.2 mEq/L (low), PCV 28% (mild non-regenerative anemia). SDMA 24 ug/dL. Blood pressure: 168/110 mmHg (hypertensive). Assessment: Chronic Kidney Disease (CKD) IRIS Stage 3, concurrent hypertension, hypokalemia, and mild anemia likely secondary to CKD. Treatment administered: 200 mL LRS subcutaneous fluids, potassium gluconate supplement started (2 mEq PO BID with food), amlodipine 0.625 mg PO SID initiated for hypertension. Prescribed Hill''s k/d renal diet. Aluminum hydroxide 100 mg PO BID with meals as phosphate binder. Owner trained on home SQ fluid administration (150 mL LRS every 48 hours). Discussed prognosis and quality-of-life indicators with owner. Recheck bloodwork and BP in 3 weeks; sooner if vomiting, seizures, or significant behavioral change observed.');
