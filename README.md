# Oturgjöld

Greiðslulausn fyrir Android snjallsíma sem nýtir HCE og NFC tækni fyrir snertilausar greiðslur

## Þróun

Þróað í Android studio og nýtir þar Gradle fyrir þarfastjórnun og byggingu.

## Prófanir

Sett upp bæði espresso fyrir end-to-end / samþættingar prófanir 
og robolectric / junit fyrir einingaprófanir sem keyra þarf úr skel.

Espresso prófin fara í androidTest pakkann undir :app einingunni í Android Studio og keyra má 
þau inni í Android studio með því að hægri smella á prófin og velja 
````
Run 'Tests in ...'
````

Robolectric og hefðbundin JUnit einingapróf fara undir test í :approbolectric og þau þarf að keyra í skel 
með skipuninni
```
./gradlew test
```
Í einhverjum tilvikum hangir gradle daemon eftir að keyrslu lýkur með þeim afleiðingum að við keyrslu prófa kemur upp villa
```
No virtual machine found
```
eða eitthvað í líkingu við það. Þetta má leysa með því að keyra
```
./gradlew --stop
```
