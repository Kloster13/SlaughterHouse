# Manuel testplan – Postman – InformationService (Traceability)

Testplan for de to gRPC-metoder i `InformationService`:

- `traceProductsFromAnimal`
- `traceAnimalsFromProduct`
## 1. `traceProductsFromAnimal` (spor produkter ud fra et dyr)

| Test-ID | Beskrivelse                                                                  | Request body           | Forventet resultat                                                                              | Status |
| ------- | ---------------------------------------------------------------------------- | ---------------------- | ----------------------------------------------------------------------------------------------- | ------ |
| TPA-01  | Dyr med to parts der spredes over to trays, som begge ender i samme produkt  | `{ "animalId": 2 }`    | `products`: 1 stk — `PKG-0002`                                                                  | x      |
| TPA-02  | Dyr hvis parts går til to forskellige produkter (ét package, ét half_animal) | `{ "animalId": 1 }`    | `products`: 2 stk — `PKG-0001` og `HALF-0001`                                                   | x      |
| TPA-03  | Dyr hvis to parts begge ender i de samme to produkter (dedup-test)           | `{ "animalId": 4 }`    | `products`: 2 stk — `HALF-0001` og `PKG-0001` (ikke 4, selvom der er 2 parts × delvist overlap) | x      |
| TPA-04  | Simpelt 1:1-spor                                                             | `{ "animalId": 5 }`    | `products`: 1 stk — `HALF-0002`                                                                 | x      |
| TPA-05  | Simpelt 1:1-spor, anden dyretype                                             | `{ "animalId": 3 }`    | `products`: 2 stk — `PKG-0003` og `HALF-0003`                                                   | x      |
| TPA-06  | Dyr der ikke findes                                                          | `{ "animalId": 9999 }` | `products`: tom liste (ingen fejl)                                                              | x      |
| TPA-07  | Grænseværdi / ubrugt id                                                      | `{ "animalId": 0 }`    | `products`: tom liste                                                                           | x      |

## 2. `traceAnimalsFromProduct` (spor dyr ud fra et produkt)

| Test-ID | Beskrivelse                                                 | Request body            | Forventet resultat                                             | Status |
| ------- | ----------------------------------------------------------- | ----------------------- | -------------------------------------------------------------- | ------ |
| TAP-01  | Produkt sammensat af to trays fra to forskellige dyr        | `{ "productId": 1 }`    | `animals`: 2 stk — dyr 1 (COW) og dyr 4 (COW)                  | x      |
| TAP-02  | Produkt hvor begge trays stammer fra samme dyr (dedup-test) | `{ "productId": 2 }`    | `animals`: 1 stk — dyr 2 (PIG)                                 | x      |
| TAP-03  | Simpelt 1:1-spor                                            | `{ "productId": 3 }`    | `animals`: 1 stk — dyr 3 (CHICKEN)                             | x      |
| TAP-04  | Half_animal-produkt, to trays, to dyr                       | `{ "productId": 6 }`    | `animals`: 2 stk — dyr 4 og dyr 1                              | x      |
| TAP-05  | Half_animal, 1:1-spor                                       | `{ "productId": 7 }`    | `animals`: 1 stk — dyr 5 (PIG)                                 | x      |
| TAP-06  | Half_animal, 1:1-spor                                       | `{ "productId": 8 }`    | `animals`: 1 stk — dyr 3 (CHICKEN)                             | x      |
| TAP-07  | Produkt der findes, men ikke er tilknyttet nogen tray endnu | `{ "productId": 4 }`    | `animals`: tom liste (package4 har ingen `product_tray`-række) | x      |
| TAP-08  | Produkt der slet ikke findes                                | `{ "productId": 9999 }` | `animals`: tom liste                                           | x      |
