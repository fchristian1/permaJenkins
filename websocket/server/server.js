import { createServer } from 'http';
import express from 'express';
import { WebSocketServer } from "ws";

const server = createServer();
const app = express();
server.on('request', app);


app.use(express.json());
app.all('/githubtrigger', (req, res) => {
    console.log('githubTrigger: ' + Date.now());
    const data = {
        type: 'githubTrigger',
        headers: req.headers,
        payload: req.body,
    }
    wss.clients.forEach((client) => {
        client.send(JSON.stringify(data));
    });
    res.send('githubTrigger');
});


const wss = new WebSocketServer({ server });
wss.on('connection', function connection(ws) {
    ws.on('error', console.error);
    ws.on('message', function message(data) {
        console.log('recived: %s', data);
    });
    ws.send('something');
});

server.listen(8080, function listening() {
    console.log('Listening on %d', server.address().port);
});