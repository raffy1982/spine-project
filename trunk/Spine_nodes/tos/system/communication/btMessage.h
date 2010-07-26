 #ifndef BTMESSAGE_H
 #define BTMESSAGE_H

typedef struct bt_message {
  char buffer[1 + SPINE_PKT_MAX_SIZE];
} bt_message;

 #endif

