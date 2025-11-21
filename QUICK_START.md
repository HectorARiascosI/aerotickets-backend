# Quick Start - Using New Constants

## For New Code (Recommended)

```java
// 1. Import new constants
import com.aerotickets.constants.ApiPaths;
import com.aerotickets.constants.ErrorMessages;
import com.aerotickets.constants.SuccessMessages;

// 2. Use them
@RestController
@RequestMapping(ApiPaths.Auth.BASE)
public class AuthController {
    
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // Use error messages
        if (!isValid(req)) {
            return ResponseEntity.badRequest()
                .body(ErrorMessages.Auth.EMAIL_REQUIRED);
        }
        
        // Use success messages
        return ResponseEntity.ok(SuccessMessages.Auth.USER_REGISTERED);
    }
}
```

## For Existing Code (Still Works)

```java
// Old constants still work - no changes needed
import com.aerotickets.constants.AuthConstants;

@RestController
@RequestMapping(AuthConstants.BASE_PATH)
public class AuthController {
    // Your code works without changes
}
```

## Constants Reference

| Purpose | Import | Example |
|---------|--------|---------|
| API Routes | `ApiPaths` | `ApiPaths.Auth.LOGIN` |
| Error Messages | `ErrorMessages` | `ErrorMessages.Auth.EMAIL_REQUIRED` |
| Success Messages | `SuccessMessages` | `SuccessMessages.Auth.USER_REGISTERED` |
| Validation | `ValidationMessages` | `ValidationMessages.Email.INVALID_FORMAT` |
| Logging | `LogMessages` | `LogMessages.Email.PASSWORD_RESET_SENT` |
| Configuration | `ConfigKeys` | `ConfigKeys.Cors.DEFAULT_ALLOWED_ORIGINS` |
| HTTP | `HttpConstants` | `HttpConstants.Headers.AUTHORIZATION` |
| Security | `SecurityPatterns` | `SecurityPatterns.PUBLIC_ENDPOINTS` |
| Business Rules | `BusinessRules` | `BusinessRules.Flight.DEFAULT_TOTAL_SEATS` |

## More Details

- **CONSTANTS_USAGE_GUIDE.md** - Complete usage guide
- **CONSTANTS_REFACTORING.md** - Migration details
- **BACKEND_REFACTORING_COMPLETE.md** - Full summary

## Key Points

✅ Old constants still work (backward compatible)  
✅ New constants are better organized  
✅ Use new constants for new code  
✅ Migrate gradually at your own pace  
✅ No breaking changes
