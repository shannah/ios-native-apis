#import "com_codenameone_ios_foundation_NSURLSession0Impl.h"
#import "com_codenameone_ios_foundation_NativeCallbacks.h"
#import "CodenameOne_GLViewController.h"


@implementation com_codenameone_ios_foundation_NSURLSession0Impl
extern JAVA_OBJECT fromNSString(CN1_THREAD_STATE_MULTI_ARG NSString *str);
-(void)downloadTaskWithURL:(NSString*)param param1:(long long)param1{

    NSURL* url = [NSURL URLWithString:param];
    [[[NSURLSession sharedSession] downloadTaskWithURL:url completionHandler: ^void (NSURL* location, NSURLResponse* response, NSError* error) {
        struct ThreadLocalData* threadStateData = getThreadLocalData();
        enteringNativeAllocations();
        JAVA_OBJECT jURL = JAVA_NULL;
        if (location != nil) {
            jURL = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG [location path]);
        }

        JAVA_OBJECT jURLResponse = JAVA_NULL;
        if (response != nil) {
            NSString* nsStrResponse = [self NSURLResponseToJSON:response];
            jURLResponse = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG nsStrResponse);
        }

        JAVA_OBJECT jError = JAVA_NULL;
        if (error != nil) {
            jError = fromNSString(CN1_THREAD_GET_STATE_PASS_ARG [self NSErrorToJSON:error]);
        }
        com_codenameone_ios_foundation_NativeCallbacks_downloadTaskCallback___long_java_lang_String_java_lang_String_java_lang_String(
            CN1_THREAD_GET_STATE_PASS_ARG (JAVA_LONG)param1,
            jURL,
            jURLResponse,
            jError
        );
        finishedNativeAllocations();

    }] resume];

}

-(NSString*) NSErrorToJSON:(NSError*) error {
     NSMutableDictionary* payload = [NSMutableDictionary dictionary];
     payload[@"localizedDescription"] = [error localizedDescription];
     payload[@"code"] = [NSNumber numberWithInt:[error code]];
     NSError* error2 = nil;
     NSData *jsonData = [NSJSONSerialization dataWithJSONObject:payload options:(NSJSONWritingOptions) 0 error:&error2];
     if (jsonData) {
         return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
     } else {
         NSLog(@"Failed to process JSON URL response: %@", error2.localizedDescription);
         return nil;
     }

}

-(NSString*) NSURLResponseToJSON:(NSURLResponse*) response {
    NSMutableDictionary* payload = [NSMutableDictionary dictionary];
    payload[@"expectedContentLength"] = [NSString stringWithFormat:@"%lld", [response expectedContentLength]];
    payload[@"suggestedFilename"] = [response suggestedFilename];
    payload[@"MIMEType"] = [response MIMEType];
    payload[@"textEncodingName"] = [response textEncodingName];
    payload[@"URL"] = [[response URL] absoluteString];
    payload[@"unknownLength"] = [NSNumber numberWithBool:[response expectedContentLength] == NSURLResponseUnknownLength];
    NSError* error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:payload options:(NSJSONWritingOptions) 0 error:&error];
    if (jsonData) {
        return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    } else {
        NSLog(@"Failed to process JSON URL response: %@", error.localizedDescription);
        return nil;
    }
}

-(BOOL)isSupported{
    return YES;
}

@end
