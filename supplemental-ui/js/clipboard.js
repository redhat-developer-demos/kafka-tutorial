;(function () {
    'use strict'
    if (window.ClipboardJS) {
      function getAll (selector, from) {
        return [].slice.call((from || document).querySelectorAll(selector))
      }
  
      function clearTooltip (e) {
        e.classList.remove('tooltipped', 'tooltipped-s', 'tooltipped-no-delay')
        e.removeAttribute('aria-label')
      }
  
      function showTooltip (elem, msg) {
        elem.classList.add('tooltipped', 'tooltipped-s', 'tooltipped-no-delay')
        elem.setAttribute('aria-label', msg)
      }
  
      function fallbackMessage (action) {
        var actionMsg = ''
        var actionKey = (action === 'cut' ? 'X' : 'C')
        if (/iPhone|iPad/i.test(navigator.userAgent)) {
          actionMsg = 'No support :('
        } else if (/Mac/i.test(navigator.userAgent)) {
          actionMsg = 'Press ⌘-' + actionKey + ' to ' + action
        } else {
          actionMsg = 'Press Ctrl-' + actionKey + ' to ' + action
        }
        return actionMsg
      }
  
      function insertCopyClipboardButton (block) {
        getAll('.content', block).forEach(function (content) {
          var $codeEl = content.getElementsByTagName('pre')[0]
          var id = block.getAttribute('id') + '-data'
          $codeEl.setAttribute('id', id)
          var button = document.createElement('button')
          button.classList.add('button', 'bd-copy')
          button.setAttribute('title', 'Copy to clipboard')
          button.dataset.clipboardTarget = '#' + id
          button.innerHTML = '<svg fill="currentColor" viewBox="0 0 896 1024" xmlns="http://www.w3.org/2000/svg">' +
            '  <path d="M128 768h256v64H128v-64z m320-384H128v64h320v-64z m128 192V448L384 640l192 192V704h320V576H576z m-288-64H128v64h160v-64zM128 704h160v-64H128v64z m576 64h64v128c-1 18-7 33-19 45s-27 18-45 19H64c-35 0-64-29-64-64V192c0-35 29-64 64-64h192C256 57 313 0 384 0s128 57 128 128h192c35 0 64 29 64 64v320h-64V320H64v576h640V768zM128 256h512c0-35-29-64-64-64h-64c-35 0-64-29-64-64s-29-64-64-64-64 29-64 64-29 64-64 64h-64c-35 0-64 29-64 64z"></path>' +
            '</svg> Copy'
          content.insertBefore(button, $codeEl)
        })
      }
  
      function addHighlightControls () {
        var $listingBlocks = getAll('.listingblock')
        $listingBlocks.forEach(function ($el, index) {
          if (!$el.getAttribute('id')) {
            $el.setAttribute('id', 'listingblock-' + index++)
          }
          insertCopyClipboardButton($el)
        })
  
        var $highlightButtons = getAll('.listingblock .bd-copy')
        $highlightButtons.forEach(function ($el) {
          $el.addEventListener('mouseleave', function () {
            clearTooltip(this)
          })
  
          $el.addEventListener('blur', function () {
            clearTooltip(this)
          })
  
          var clipboardSnippets = new ClipboardJS($el, {
            text: function(trigger) {
              var codePre = trigger.nextElementSibling;
              var code = codePre.firstChild

              var divCode = trigger.parentNode.parentNode
              var classDocCode = divCode.classList

              for (var index=0;index<classDocCode.length;index++) {
                var attribute = classDocCode[index];
                if (attribute === 'lines_space') {
                  var lines = codePre.innerText.split("\n")
                  var command = [];
                  for (var index in lines) {
                    if (!(lines[index].trim().length == 0)) {
                      command.push(lines[index]);
                    } else {
                      // When an empty line is found we can stop copying
                      break;
                    }
                  }

                  return command.join("\n")
                } else {
                  if (attribute.startsWith('lines')) {
                    var lastLine = attribute.substring(attribute.indexOf('_') + 1);
                    var lastLineIndex = parseInt(lastLine)

                    var command = [];
                    var lines = codePre.innerText.split("\n")
                    for (var i=0; i<lastLineIndex; i++) {
                      command.push(lines[i]);
                    }
                    return command.join("\n")
                  }
                }
              }

              return trigger.nextElementSibling.innerText
          }
          });

          clipboardSnippets.on('success', function (e) {
            e.clearSelection()
            showTooltip(e.trigger, 'Copied!')
          })
          clipboardSnippets.on('error', function (e) {
            showTooltip(e.trigger, fallbackMessage(e.action))
          })
        })
      }
  
      addHighlightControls()
    }
  })()
  