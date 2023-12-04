## Android-App for the detection of the brushed quad, tooth and surface
The user interface of the app is shown below (splashscreen, user list, creating a new user).
<p align="center">
  <img src="./media/ui_intro.png" width="200px">
  <img src="./media/ui_users.png" width="200px"> 
  <img src="./media/ui_new.png" width="200px">
</p>
The users profile (name and toothbrush length) and the brushing-events (start- and end-time) are managed in a SQLite database.

Using a specially preparated toothbrush, as shown below, the app can detect its colors using OpenCV.

<p align="center">
  <img src="./media/brush.png" width="500px">
</p>

By additionally detecting the users eyes, the quadrant schema can be layed over.

<p align="center">
  <img src="./media/schema.png" width="500px">
</p>

This quadrant schema gets positioned and appropriately sized with the help of the eye distance and the toothbrush length.\
Directly detecting the users mouth didn't show success as the mouth is mostly covered in the process of toothbrushing.

The brushed tooth is detected by analysing the distance of the outer toothbrush-mark to the calculated mouth center.\
As the "travelled" distance of the toothbrush-end isn't linear\
(going from the first tooth to the second has a much higher distance as from the 7th to the last)\
a parabola is used to project the linear division into a more realistic distribution.

<p align="center">
  <img src="./media/tooth.png" width="500px">
</p>

After detecting the quadrant and tooth, the brushed surface can be calculated. This is done by comparing the two different mark colors.\
By analysing their color "volume" and positioning relative to each other, the brushed surface can be calculated.\
This additionally allows to second check the previous quad and tooth results.

Shown below is a full analysis output, with the edgepoints of the schema and the detected brush colors.\
The calculated brushed quad, tooth and surface are shown in the top left corner.

<p align="center">
  <img src="./media/analysis.png" width="700px">
</p>

The actual code is located in <a href="app/src/main/java/hs_kempten/ibrush/">app/src/main/java/hs_kempten/ibrush/</a>.