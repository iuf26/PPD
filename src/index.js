const Koa = require("koa");
const app = new Koa();
const server = require("http").createServer(app.callback());
const WebSocket = require("ws");
const wss = new WebSocket.Server({ server });
const Router = require("koa-router");
const cors = require("koa-cors");
const bodyparser = require("koa-bodyparser");
const jwt = require("koa-jwt");
const jsonwebtoken = require("jsonwebtoken");
const corsBasic = require("cors");

const jwtSecret = "secret123";

app.use(bodyparser());
app.use(cors());
app.use(async (ctx, next) => {
  const start = new Date();
  await next();
  const ms = new Date() - start;
});

app.use(async (ctx, next) => {
  await new Promise((resolve) => setTimeout(resolve, 2000));
  await next();
});

app.use(async (ctx, next) => {
  try {
    await next();
  } catch (err) {
    ctx.response.body = {
      issue: [{ error: err.message || "Unexpected error" }],
    };
    ctx.response.status = 500;
  }
});

class Item {
  constructor({ id, text, date, version }) {
    this.id = id;
    this.text = text;
    this.date = date;
    this.version = version;
  }
}
class Flight {
  constructor({ id, airlineCode, estimatedArrival, landed, userId }) {
    this.id = id;
    this.airlineCode = airlineCode;
    this.estimatedArrival = estimatedArrival;
    this.landed = landed;
    this.userId = userId;
  }
}
function getRandonString(length) {
  var chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012";
  var charLength = chars.length;
  var result = "";
  for (var i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * charLength));
  }
  return result;
}
function getRandomInt(max) {
  return Math.floor(Math.random() * max);
}
const truthValues = [true, false];
const userIds = [1, 2];
const createRandomFlights = (nr) => {
  let id = 1;
  const flights = [];
  while (nr) {
    flights.push(
      new Flight({
        id,
        airlineCode: getRandonString(10),
        landed: truthValues[getRandomInt(2)],
        estimatedArrival: new Date(),
        userId: userIds[getRandomInt(2)],
      })
    );
    id++;
    nr--;
  }
  return flights;
};

const items = createRandomFlights(100);

let lastUpdated = items[items.length - 1].date;
let lastId = items[items.length - 1].id;
const pageSize = 10;

const broadcast = (data) =>
  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify(data));
    }
  });

const publicRouter = new Router();
const router = new Router();
router.use(jwt({ secret: jwtSecret, algorithms: ["HS256"] }));
router.get("/jwt", (ctx) => {
  ctx.response.body = {
    token: jsonwebtoken.sign({ user: "johndoe" }, jwtSecret),
  };
});

router.get("/item", (ctx) => {
  // const ifModifiedSince = ctx.request.get('If-Modified-Since');
  // if (ifModifiedSince && new Date(ifModifiedSince).getTime() >= lastUpdated.getTime() - lastUpdated.getMilliseconds()) {
  //   ctx.response.status = 304; // NOT MODIFIED
  //   return;
  // }
  // const text = ctx.request.query.text;
  // const page = parseInt(ctx.request.query.page) || 1;
  // ctx.response.set('Last-Modified', lastUpdated.toUTCString());
  // const sortedItems = items
  //   .filter(item => text ? item.text.indexOf(text) !== -1 : true)
  //   .sort((n1, n2) => -(n1.date.getTime() - n2.date.getTime()));
  // const offset = (page - 1) * pageSize;
  // ctx.response.body = {
  //   page,
  //   items: sortedItems.slice(offset, offset + pageSize),
  //   more: offset + pageSize < sortedItems.length
  // };

  ctx.response.body = items;

  ctx.response.status = 200;
});

router.get("/item/:id", async (ctx) => {
  const itemId = ctx.request.params.id;
  const item = items.find((item) => itemId === item.id);
  if (item) {
    ctx.response.body = item;
    ctx.response.status = 200; // ok
  } else {
    ctx.response.body = {
      issue: [{ warning: `item with id ${itemId} not found` }],
    };
    ctx.response.status = 404; // NOT FOUND (if you know the resource was deleted, then return 410 GONE)
  }
});

const accounts = [
  { user: "i@done.com", pass: "123" },
  { user: "b@done.com", pass: "123" },
];
publicRouter.post("/login", async (ctx) => {
  const body = ctx.request.body;
  let userId = -1;
  if (body.user === accounts[0].user) userId = 1;
  if (body.user === accounts[1].user) userId = 2;
  if (
    accounts.find((elem) => elem.pass === body.pass && elem.user === body.user)
  ) {
    ctx.response.body = {
      token: jsonwebtoken.sign({ user: "johndoe" }, jwtSecret),
      userId,
    };
    ctx.response.status = 200;

    return;
  }

  ctx.response.status = 400;
  return;
});

const createItem = async (ctx) => {
  const item = ctx.request.body;
  if (item.id > 0) {
    const index = items.findIndex(
      (elem) => elem.id.toString() === item.id.toString()
    );
    if (index > -1) {
      items[index] = item;
    }
    ctx.response.body = item;
    ctx.response.status = 200; // CREATED
    broadcast({ event: "updated", payload: { item } });
  } else {
    item.id = items.length + 1;
    item.userId = parseInt(ctx.request.query.userId);
    console.log(item);
    items.push(item);
    console.log(items.length);
    ctx.response.body = item;
    ctx.response.status = 201; // CREATED
    broadcast({ event: "created", payload: { item } });
  }
};

router.post("/item", async (ctx) => {
  await createItem(ctx);
});

router.put("/item/:id", async (ctx) => {
  const id = ctx.params.id;
  const item = ctx.request.body;
  item.id = parseInt(item.id);
  const index = items.findIndex((item) => item.id.toString() === id.toString());
  if (index === -1) {
    ctx.response.body = { issue: [{ error: `item with id ${id} not found` }] };
    ctx.response.status = 400; // BAD REQUEST
    return;
  }

  items[index] = item;
  lastUpdated = new Date();
  ctx.response.body = item;
  ctx.response.status = 200; // OK
  broadcast({ event: "updated", payload: { item } });
});

router.del("/item/:id", (ctx) => {
  const id = ctx.params.id;
  const index = items.findIndex((item) => id === item.id);
  if (index !== -1) {
    const item = items[index];
    items.splice(index, 1);
    lastUpdated = new Date();
    broadcast({ event: "deleted", payload: { item } });
  }
  ctx.response.status = 204; // no content
});

app.use(router.routes());
app.use(publicRouter.routes());
app.use(router.allowedMethods());

server.listen(3000);
