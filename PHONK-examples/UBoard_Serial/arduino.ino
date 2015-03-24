/* load this sketch in your Arduino */

static int counter = 0;
int led = 13;

void setup() {
  Serial.begin(9600);
  pinMode(led, OUTPUT);
  
}

void loop() {
  Serial.print("Tick #");
  Serial.print(counter++, DEC);
  Serial.print("\n");

  
  if (Serial.peek() != -1) {
    Serial.print("Read: ");
    String inputString2 = "";
    do {
      char inchar = (char) Serial.read();
      inputString2 += inchar;
      
    } while (Serial.peek() != -1);
   
    if(inputString2.startsWith("ledon")){
      digitalWrite(led, HIGH);
    } else if(inputString2.startsWith("ledoff")){
      digitalWrite(led, LOW);
    } 
    
    Serial.print(inputString2);
    Serial.print("\n");
  }
    
  delay(1000);
}