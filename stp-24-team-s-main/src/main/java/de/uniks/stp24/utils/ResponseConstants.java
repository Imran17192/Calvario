package de.uniks.stp24.utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/* maps with text outputs for different server responses depending on controller/screen
there are on /api/v1 these 19 codes:
CODE -> STRING
400 -> Bad Request
401 -> Unauthorized
403 -> Forbidden
404 -> Not Found
405 -> Method Not Allowed
406 -> Not Acceptable
408 -> Request Timeout
409 -> Conflict
410 -> Gone
412 -> Precondition Failed
413 -> Payload Too Large
415 -> Unsupported Media Type
418 -> I Am A Teapot
422 -> Unprocessable Entity
500 -> Internal Server Error
501 -> Not Implemented
502 -> Bad Gateway
503 -> Service Unavailable
504 -> Gateway Timeout
 */
@Singleton
public class ResponseConstants {

    final public Map<Integer, String> resStdText;

    final public Map<Integer, String> respLogin = Map.of(400,"pirate.general.invalid.password",
      401, "pirate.general.invalid.password",
      404, "404",
      429, "pirate.general.rate.limit.reached",
      -1 , "pirate.login.no.username.or.password",
      201, "try.login");
    final public Map<Integer, String> respSignup = Map.of(400,"pirate.general.invalid.password",
      404, "404",
      409, "pirate.general.name.exists.already",
      429,"pirate.general.rate.limit.reached",
      201, "try.login",
      -2, "pirate.register.passwords.dont.match",
      -1 , "please put in name or/and password");

    final public Map<Integer, String> respEditAcc = Map.of(400, "pwd.8characters",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.user",
      404, "404",
      409, "username.in.use.by.another.user",
      429,"pirate.general.rate.limit.reached",
      -1, "put.in.username.password",
      -2, "enter.password");

    final public Map<Integer, String> respCreateGame = Map.of(400, "invalid.password",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.user",
      404, "404",
      409, "gamename.already.exists",
      429,"pirate.general.rate.limit.reached",
      -1, "put.in.username.password",
      -2, "pirate.register.passwords.dont.match");

    final public Map<Integer, String> respEditGame = Map.of(400, "invalid.password",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.user",
      404, "404",
      409, "gamename.already.exists",
      429,"pirate.general.rate.limit.reached",
      -1, "put.in.username.password",
      -2, "pirate.register.passwords.dont.match");

    final public Map<Integer, String> respDelGame = Map.of(400 ,"invalid.password",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.game",
      404, "404",
      429, "rate.limit.reached");

    final public Map<Integer, String> respGetGame = Map.of(400 ,"invalid.password",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.game",
      404, "404",
      429, "rate.limit.reached");

    @Inject
    public ResponseConstants() {
        Map<Integer, String> tmp = new HashMap<>();
        tmp.put(400, "400");
        tmp.put(401, "401");
        tmp.put(403, "403");
        tmp.put(404, "404");
        tmp.put(405, "405");
        tmp.put(406, "406");
        tmp.put(408, "408");
        tmp.put(409, "409");
        tmp.put(410, "410");
        tmp.put(412, "412");
        tmp.put(413, "413");
        tmp.put(415, "415");
        tmp.put(418, "418");
        tmp.put(422, "422");
        tmp.put(500, "500");
        tmp.put(501, "501");
        tmp.put(502, "502");
        tmp.put(503, "503");
        tmp.put(504, "504");
        resStdText = Map.copyOf(tmp);
    }

}