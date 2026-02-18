#!/bin/bash

# Required parameters:
# @raycast.schemaVersion 1
# @raycast.title Shorten Clipboard URL
# @raycast.mode silent

# Optional parameters:
# @raycast.icon 🔗
# @raycast.packageName jjalb
# @raycast.argument1 { "type": "text", "placeholder": "Author", "optional": true }

# Documentation:
# @raycast.description Shorten the URL in your clipboard and replace it with the short link
# @raycast.author ddingmin
# @raycast.authorURL https://github.com/ddingmin

API_URL="https://jjalb.ddmz.org/api/shorten"

URL=$(pbpaste)

if [[ -z "$URL" ]]; then
  echo "Clipboard is empty"
  exit 1
fi

RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d "{\"url\": \"$URL\"$([ -n "$1" ] && echo ", \"author\": \"$1\"")}")

HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [[ "$HTTP_CODE" != "201" ]]; then
  echo "Failed to shorten URL (HTTP $HTTP_CODE)"
  exit 1
fi

SHORT_URL=$(echo "$BODY" | python3 -c "import sys,json; print(json.load(sys.stdin)['shortUrl'])" 2>/dev/null)

if [[ -z "$SHORT_URL" ]]; then
  echo "Failed to parse response"
  exit 1
fi

echo -n "$SHORT_URL" | pbcopy
echo "Copied: $SHORT_URL"
