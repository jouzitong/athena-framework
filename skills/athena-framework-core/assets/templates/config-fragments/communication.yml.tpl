athena:
  communication:
    enabled: true
    allow-direct-content: false
    email:
      enabled: false
      from: ${MAIL_FROM:}
      html: true
    sms:
      enabled: false
      access-key-id: ${SMS_ACCESS_KEY_ID:}
      access-key-secret: ${SMS_ACCESS_KEY_SECRET:}
      sign-name: ${SMS_SIGN_NAME:}
    wecom:
      enabled: false
      corp-id: ${WECOM_CORP_ID:}
      corp-secret: ${WECOM_CORP_SECRET:}
      agent-id: ${WECOM_AGENT_ID:0}
