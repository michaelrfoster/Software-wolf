//Copyright 2018 Google LLC.SPDX-License-Identifier: Apache-2.0

'use strict';

const {dialogflow} = require('actions-on-google');
const functions = require('firebase-functions');
const app = dialogflow({debug: true});

var admin = require("firebase-admin");

var serviceAccount = require("./software-wolf-firebase-adminsdk-y70tm-b842610f8c.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://software-wolf.firebaseio.com"
});

const db = admin.database();

app.intent('write_to_firebase', (conv, {entry}) => {
  db.ref("label").set(`${entry}`);
  conv.add(`Wrote "${entry}" to the Firebase.`);
});

app.intent('start lobby', (conv) => {
  db.ref("game_state").set("lobby_state");
  conv.close("Started the lobby. Players can now join. When everyone is ready, ask me to start the game.");
});


app.intent('start game', async (conv) => {
  
  conv.add("Everyone ready? Let's go!");
  conv.close("Long ago, there were werewolves or somesuch blah blah et cetera. When everyone has seen their own roles, ask me to start the day.")
  //handle role assignment here
  db.ref("vote").set("abstain");
  db.ref("wvote").set("none");
  conv.user.storage.day = 0;

  let _ = await db.ref("players").once("value", (snapshot) => {
    var playerCount = snapshot.numChildren();
    var playerList = snapshot.val();
    console.log(playerCount);
    var numOfWolves = Math.floor(playerCount / 3);
    conv.user.storage.wolvesLeft = numOfWolves;
    conv.user.storage.vilsLeft = playerCount - numOfWolves;
    var bucket = [];
    var chosen = [];
    for (var i=0;i<playerCount;i++) {
      bucket.push(i);
    }
    for (var i=0;i<numOfWolves;i++) {
      var randomIndex = Math.floor(Math.random()*bucket.length);
      chosen.push(bucket.splice(randomIndex, 1)[0]);
    }
    //console.log(chosen);
    //console.log(bucket);
    //console.log(playerList);
    var index = 0;
    for (var player in playerList) {
      let cref = db.ref("players").child(player);
      cref.update({rng: index});
      index++;
    }
    index = 0
    for (var player in playerList) {
      //console.log(player);
      let cref = db.ref("players").child(player);
      cref.child("rng").once("value", (snapshot) => {
        if (chosen.includes(snapshot.val())) {
          //console.log('wolf');
          cref.update({role: "Werewolf"});
        } else {
          //console.log('vil');
          cref.update({role: "Villager"});
        };
        cref.update({rng: null});
      index++;
      });
    };
  });
  db.ref("game_state").set("role_state");
});

app.intent('start day', async (conv) => {
  //count votes here
  let __ = await db.ref("players").once("value", async (snapshot) => {
    let playerList = snapshot.val();
    var voteBucket = [];
    for (var player in playerList) {
      let wvote = playerList[player].werewolf_vote;
      if (wvote != null) {
        voteBucket.push(wvote);
      }
    }
    let v = mode(voteBucket);
    if (v != null){db.ref('wvote').set(v)};
  });

  db.ref("game_state").set("day_state");
  conv.user.storage.day++;
  let _ = await db.ref("wvote").once("value", async (snapshot) => {
    if (snapshot.val() == 'none'){
      conv.close(`<speak>It's the dawn of the <say-as interpret-as="ordinal">${conv.user.storage.day}</say-as> day. When you're ready, ask me to count the votes.</speak>`);
    } else {
      conv.add(`<speak>It's the dawn of the <say-as interpret-as="ordinal">${conv.user.storage.day}</say-as> day.</speak>`);
      conv.close(`${snapshot.val()} was found dead. It must have been the werewolves. When everyone's ready, ask me to count the votes.`)
      //handle player death here
      let __ = await db.ref("players").once("value", (snapshot2) => {
        let playerList = snapshot2.val();
        for (var player in playerList) {
          if (playerList[player].name == snapshot.val()) {
            if (playerList[player].role == 'Werewolf') {
              conv.user.storage.wolvesLeft--;
            } else {
              conv.user.storage.vilsLeft--;
            }
            db.ref("players").child(player).update({dead: "true"});
            if (conv.user.storage.wolvesLeft == 0) {
              db.ref("game_state").set("villager_win_state");
            }
            if (conv.user.storage.wolvesLeft == conv.user.storage.vilsLeft) {
              db.ref("game_state").set("werewolf_win_state");
            }
            break
          }
        }
      });
    } 
  });
});

app.intent('start vote', async (conv) => {
  //count votes here
  let __ = await db.ref("players").once("value", async (snapshot) => {
    let playerList = snapshot.val();
    var voteBucket = [];
    for (var player in playerList) {
      voteBucket.push(playerList[player].vote);
    }
    let v = mode(voteBucket);
    db.ref('vote').set(v);
  
    let _ = await db.ref("vote").once("value", (snapshot2) => {
      let vote = snapshot2.val();
      //console.log(vote);
      conv.add("The votes are in.");
      //console.log(vote.toLowerCase());
      if(vote.toLowerCase() == "abstain") {
        conv.close("There was no concensus, no one dies today. When you're ready, ask me to end the day.");
      } else {
        conv.close(`${vote} is dead. When everyone is ready, ask me to end the day.`);
        //handle player death here
        for (var player in playerList) {
          if (playerList[player].name == vote) {
            if (playerList[player].role == 'Werewolf') {
              conv.user.storage.wolvesLeft--;
            } else {
              conv.user.storage.vilsLeft--;
            }
            db.ref("players").child(player).update({dead: "true"});
            if (conv.user.storage.wolvesLeft == 0) {
              db.ref("game_state").set("villager_win_state");
            }
            if (conv.user.storage.wolvesLeft == conv.user.storage.vilsLeft) {
              db.ref("game_state").set("werewolf_win_state");
            }
            break
          }
        }       
      }
    });
  });
});

function mode(array)
{
    if(array.length == 0)
        return null;
    var modeMap = {};
    var maxEl = array[0], maxCount = 1;
    for(var i = 0; i < array.length; i++)
    {
        var el = array[i];
        if(modeMap[el] == null)
            modeMap[el] = 1;
        else
            modeMap[el]++;  
        if(modeMap[el] > maxCount)
        {
            maxEl = el;
            maxCount = modeMap[el];
        }
    }
    return maxEl;
}

app.intent('start night', async (conv) => {
  db.ref("game_state").set("night_state");
  conv.add(`<speak>The sun has set. Everyone close your eyes. Werewolves, you have ten seconds to choose someone to kill.
  Ten.<break time='1'/>
  Nine.<break time='1'/>
  Eight.<break time='1'/>
  Seven.<break time='1'/>
  Six.<break time='1'/>
  Five.<break time='1'/>
  Four.<break time='1'/>
  Three.<break time='1'/>
  Two.<break time='1'/>
  One.<break time='1'/>Time is up. Everyone open your eyes. Say "next" to continue.</speak>`);
});

app.intent('start night - next', (conv) => {
  conv.followup('day');
});

app.intent('end game', (conv) => {
  db.ref("game_state").set("lobby_state");
  db.ref("players").set(null);
  db.ref("vote").set("abstain");
  db.ref("wvote").set("none");
  conv.close("Game reset.");
});

exports.actionsOracle = functions.https.onRequest(app);
