
#include <SoftwareSerial.h> // use the software uart
SoftwareSerial bluetooth(2, 4); // RX, TX

unsigned long previousMillis = 0;        // will store last time
const long interval = 3000;           // interval at which to delay
static uint32_t tmp; // increment this

const int IN_BAT = A0;

const int RL_LL_CHAR = 3;
const int RL_HL_KLAX = 1;
const int RL_LL_PF = 9;
const int RL_LL_CLD = 10;
const int RL_LL_CLG = 11;
const int RL_HL_CLEF = 12;
const int RL_HL_12V = 13;
const int RL_HL_PRT1 = 0;
const int RL_LL_PRT2 = A4;
const int RL_LL_BTN = A5;

const int BTN_PAGE = 8;
const int BTN_CLG = 7;
const int BTN_CLD = 6;
const int BTN_PF = 5;

int last_page = 1;
int last_clg = 1;
int last_cld = 1;
int last_pf = 1;

float fVoltageLu = 0;
char cVoltageLu[10];
String sVoltageLu;

float voltage(int analogValue) {
  return 0.0048 * analogValue;
}

void setup() {
  //Serial.begin(115200);

  // Les relais
  pinMode(RL_HL_12V, OUTPUT);
  pinMode(RL_HL_CLEF, OUTPUT);
  pinMode(RL_LL_CLG, OUTPUT);
  pinMode(RL_LL_CLD, OUTPUT);
  pinMode(RL_LL_PF, OUTPUT);

  pinMode(RL_LL_CHAR, OUTPUT);
  pinMode(RL_HL_KLAX, OUTPUT);

  pinMode(RL_HL_PRT1, OUTPUT);
  pinMode(RL_LL_PRT2, OUTPUT);

  pinMode(RL_LL_BTN, OUTPUT);

  // Les boutons
  pinMode(BTN_PAGE, INPUT_PULLUP);
  pinMode(BTN_CLG, INPUT_PULLUP);
  pinMode(BTN_CLD, INPUT_PULLUP);
  pinMode(BTN_PF, INPUT_PULLUP);

  pinMode(IN_BAT, INPUT);

  pinMode(13, OUTPUT); // for LED status

  bluetooth.begin(9600); // start the bluetooth uart at 9600 which is its default
  delay(200); // wait for voltage stabilize
 // bluetooth.print("AT+NAMEMOTO"); // place your name in here to configure the bluetooth name.
  // will require reboot for settings to take affect.
  //RAZ des relais
  digitalWrite(RL_LL_CHAR, HIGH);
  digitalWrite(RL_HL_KLAX, LOW);
  digitalWrite(RL_LL_PF, HIGH);
  digitalWrite(RL_LL_CLD, HIGH);
  digitalWrite(RL_LL_CLG, HIGH);
  digitalWrite(RL_HL_CLEF, LOW);
  digitalWrite(RL_HL_12V, LOW);
  digitalWrite(RL_HL_PRT1, LOW);
  digitalWrite(RL_LL_PRT2, HIGH);
  digitalWrite(RL_LL_BTN, HIGH);

  delay(3000); // wait for settings to take affect.

}

void loop() {
  delay(10);



  //Serial.println("Itération");

  if (last_page != digitalRead(BTN_PAGE)) {
    Serial.println("BTN_PAGE");
    // Changement d'etat detecté
    if (digitalRead(BTN_PAGE) == 1) {
      bluetooth.print("BTN_PAGE_0;");
    }
    else {
      bluetooth.print("BTN_PAGE_1;");
    }
    last_page = digitalRead(BTN_PAGE);
  }

  
  if (last_clg != digitalRead(BTN_CLG)) {
    digitalWrite(RL_LL_CLG, digitalRead(BTN_CLG));
    Serial.println("BTN_CLG");
    // Changement d'etat detecté
    if (digitalRead(BTN_CLG) == 1) {
      bluetooth.print("BTN_CLG_0;");
    }
    else {
      bluetooth.print("BTN_CLG_1;");
    }
    last_clg = digitalRead(BTN_CLG);
  }

  
  if (last_cld != digitalRead(BTN_CLD)) {
    digitalWrite(RL_LL_CLD, digitalRead(BTN_CLD));
    Serial.println("BTN_CLD");
    // Changement d'etat detecté

    if (digitalRead(BTN_CLD) == 1) {
      bluetooth.print("BTN_CLD_0;");
    }
    else {
      bluetooth.print("BTN_CLD_1;");
    }

    last_cld = digitalRead(BTN_CLD);
  }

  
  if (last_pf != digitalRead(BTN_PF)) {
    digitalWrite(RL_LL_PF, digitalRead(BTN_PF));
    Serial.println("BTN_PF");
    // Changement d'etat detecté

    if (digitalRead(BTN_PF) == 1) {
      bluetooth.print("BTN_PF_0;");
    }
    else {
      bluetooth.print("BTN_PF_1;");
    }

    last_pf = digitalRead(BTN_PF);
  }


  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;

    fVoltageLu = voltage(analogRead(IN_BAT));
    dtostrf(fVoltageLu, 4, 6, cVoltageLu); //4 is mininum width, 6 is precision
    sVoltageLu = cVoltageLu;
    bluetooth.print(sVoltageLu + ";");
    //  Serial.println(sVoltageLu);
    //  Serial.println(fVoltageLu);
  }

  // RECEPTION BT
  if (bluetooth.available()) { // check if anything in UART buffer
    byte b = bluetooth.read();

    switch (b) {
      case 'a':
        digitalWrite(RL_HL_PRT1, LOW);
        break;
      case 'b':
        digitalWrite(RL_HL_PRT1, HIGH);
        break;

      case 'c':
        digitalWrite(RL_HL_KLAX, LOW);
        break;
      case 'd':
        digitalWrite(RL_HL_KLAX, HIGH);
        break;

      case 'e':
        digitalWrite(RL_LL_CHAR, HIGH);
        break;
      case 'f':
        digitalWrite(RL_LL_CHAR, LOW);
        break;

      case 'g':
        digitalWrite(RL_LL_PF, HIGH);
        break;
      case 'h':
        digitalWrite(RL_LL_PF, LOW);
        break;

      case 'i':
        digitalWrite(RL_LL_CLD, HIGH);
        break;
      case 'j':
        digitalWrite(RL_LL_CLD, LOW);
        break;

      case 'k':
        digitalWrite(RL_LL_CLG, HIGH);
        break;
      case 'l':
        digitalWrite(RL_LL_CLG, LOW);
        break;

      case 'm':
        digitalWrite(RL_HL_CLEF, LOW);
        break;
      case 'n':
        digitalWrite(RL_HL_CLEF, HIGH);
        break;

      case 'o':
        digitalWrite(RL_HL_12V, LOW);
        break;
      case 'p':
        digitalWrite(RL_HL_12V, HIGH);
        break;

      case 'q':
        digitalWrite(RL_LL_BTN, HIGH);
        break;
      case 'r':
        digitalWrite(RL_LL_BTN, LOW);
        break;

      case 's':
        digitalWrite(RL_LL_PRT2, HIGH);
        break;
      case 't':
        digitalWrite(RL_LL_PRT2, LOW);
        break;
    }
  }

}
