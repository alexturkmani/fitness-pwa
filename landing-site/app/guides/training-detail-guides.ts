import type { Guide } from './content';

// Retain batch metadata locally without changing the shared content contract.
type ExpansionGuide = Guide & { contentBatch: 'expansion-2' };

const articles: ExpansionGuide[] = [
  {
    slug: 'log-warm-up-sets-separately',
    title: 'Logging warm-up sets separately from working sets',
    metaTitle: 'Log Warm-Up Sets Separately from Work Sets',
    description: 'Separate preparation from working sets with clear labels, an illustrative session audit and a practical free Android logging workflow.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A workout history can look busier simply because you started recording preparation sets. If last week contains only working sets and this week includes every warm-up, the extra entries describe a recording change. A useful log preserves both kinds of work while making their roles clear.',
    takeaway: 'Label preparation and working sets consistently. Compare the same category across sessions, and keep the warm-up instructions separate from the results you actually record.',
    sections: [
      { id: 'define-roles', title: 'Decide what each set label means', paragraphs: [
        'Use your existing programme or trainer explanation to identify preparation sets and working sets. A lighter load does not automatically establish the category, and the first set is not necessarily a warm-up. Write a short definition in a companion note so you apply the distinction the same way next time.',
        'The NSCA describes warm-ups as preparation for the activity that follows. That supports distinguishing their purpose, but it does not specify your personal warm-up sequence. This guide concerns recording the sequence you have been taught, rather than choosing how many preparation sets to perform.'
      ] },
      { id: 'entry-method', title: 'Choose a label that survives later review', paragraphs: [
        'Check the fields available in your tracker before starting. If there is no verified warm-up classification, keep a dated companion record identifying which entries were preparation. A clear exercise label can also carry context where supported. Do not assume Nexal automatically excludes selected sets from any session total.',
        'Use the same method for every visit in the comparison period. Alternating between logging all sets and logging only working sets leaves an unexplained gap. If you deliberately change your method, date the change and preserve the earlier convention so historical entries remain understandable.'
      ] },
      { id: 'session-audit', title: 'Read a session with two different set roles', paragraphs: [
        'Consider an illustrative record with two preparation entries at 20 kg and 30 kg, followed by three working entries at 40 kg. The history contains five performed sets, but only three belong to the working-set category. These invented figures explain bookkeeping and are not a suggested warm-up or workload.',
        'If the previous comparable session contains three working sets only, compare those three with the current three. You can acknowledge the newly recorded preparation without describing the session as two extra working sets. Never delete real preparation merely to make a headline total look consistent.'
      ], bullets: ['Match the exercise variation and equipment.', 'Identify the role of each performed set.', 'Check whether earlier sessions used the same recording convention.'] },
      { id: 'unexpected-change', title: 'Preserve an unplanned preparation change', paragraphs: [
        'Sometimes an instructor adds a practice set or changes the preparation sequence. Record what happened and who clarified its purpose. If you are uncertain whether an entry was practice or working work, mark that uncertainty in your own notes rather than assigning a confident category from memory.',
        'At review, separate a change in recording detail from a change in the programme. An extra logged practice set cannot establish that your working workload increased. Ask your trainer how to interpret the session if the distinction affects their next instructions.'
      ] },
      { id: 'try-tracker', title: 'Test one clearly labelled workout in Nexal', paragraphs: [
        'Download Nexal for Android and try its free manual workout logging, custom workouts and history with a routine you already use. After saving, check whether you can identify the preparation entries using your chosen companion reference. This is a more useful first test than filling history with hypothetical sessions.',
        'Premium AI workout planning is optional when you need help organising a routine. Recording warm-up context does not require buying a generated plan. Keep the logging convention simple enough to maintain during an ordinary visit, then use matching working entries for your next review.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal workout logging and planning', text: 'Record actual sessions free on Android and consider Premium planning when needed.' },
    sources: [{ href: 'https://dxpprod.nsca.com/education/articles/kinetic-select/introduction-to-dynamic-warm-up/', label: 'NSCA: the purpose of a dynamic warm-up' }],
    faqs: [
      { question: 'Should warm-up sets count as working sets?', answer: 'Keep their roles distinct according to your programme. A combined session count should not be read as a working-set count.' },
      { question: 'Does Nexal automatically filter warm-up sets?', answer: 'This workflow does not assume a dedicated warm-up filter. Preserve classification in your companion notes where the supported fields do not capture it.' }
    ]
  },
  {
    slug: 'record-assisted-pull-up-progress',
    title: 'Recording assisted pull-ups without misleading progress',
    metaTitle: 'Assisted Pull-Up Logging Without Confusion',
    description: 'Record assistance, machine identity and repetitions without treating the assistance stack as added lifting weight.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'Assisted pull-ups create an unusual logging problem: the number you select may represent help rather than added resistance. A history that treats every larger stack number as a heavier lift can tell the wrong story. Start by identifying exactly what your equipment label represents.',
    takeaway: 'Write assistance explicitly, keep the machine and grip consistent, and compare completed repetitions alongside the assistance setting. Avoid converting the record into an unsupported net lifting figure.',
    sections: [
      { id: 'assistance-label', title: 'Confirm that the setting represents assistance', paragraphs: [
        'Read the machine placard and ask staff how its selector works. The Cybex Dip/Chin Assist manufacturer page describes assistance and distinct hand positions, illustrating why an assisted station needs a different interpretation from an ordinary resistance stack. Check your actual model rather than borrowing another machine’s instructions.',
        'If staff confirm that a larger setting supplies more help, record that direction in your equipment reference. Label the quantity assistance kg or assistance lb where supported. If the app field cannot convey the distinction, preserve the full meaning in a dated companion note instead of leaving an unexplained positive weight.'
      ] },
      { id: 'identity', title: 'Separate machine assistance from band assistance', paragraphs: [
        'A counterweighted station and an elastic band are different setups. Keep separate exercise identities for them, including the machine model or the specific band. Do not assign a band an invented fixed kilogram assistance value so it can share the machine’s history.',
        'Include the grip and whether the machine uses a knee or foot support. These details help you recognise a comparable attempt. They do not instruct you to change the movement: retain the setup you have been taught and ask an instructor when you are uncertain about a variation.'
      ] },
      { id: 'compare-example', title: 'Compare assistance and repetitions together', paragraphs: [
        'In an illustrative example, visit A records eight repetitions with 35 kg assistance and visit B records eight with 30 kg assistance on the same station. The second entry used a lower assistance setting. That is the defensible observation; calling it a 5 kg increase in added load would misdescribe the machine.',
        'Now imagine visit C records five repetitions with 30 kg assistance. The assistance is unchanged but the completed repetitions differ. Keep both numbers visible rather than declaring progress from the selector alone. These figures are invented notation examples, not targets or an instruction to reduce assistance.'
      ] },
      { id: 'avoid-net-load', title: 'Leave uncertain resistance calculations out', paragraphs: [
        'Subtracting a stack label from body mass can look precise while ignoring what the equipment documentation actually establishes. Unless a manufacturer and qualified instructor explain a suitable interpretation, keep body mass and assistance as separate observations. You do not need a calculated net load to maintain a useful diary.',
        'Use a comparison checklist: same machine, same assistance unit, same grip, same support and same repetition definition. If one condition changes, describe it. A different model with the same selector number starts a new equipment comparison rather than automatically continuing the old series.'
      ] },
      { id: 'android-test', title: 'Make assistance readable in your first app session', paragraphs: [
        'Nexal’s free Android custom workouts, manual logging and history can hold your established routine and completed results. Keep the assistance convention beside the session in your own reference if necessary. Reopen the saved entry and ask whether you could distinguish assistance from added weight without remembering today’s visit.',
        'Install Nexal from Google Play to test that recording workflow with a real session. Premium AI workout plans are optional planning help. The practical app test here is whether the history plus your reference preserves the meaning of the equipment setting, without promising automatic assistance calculations.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See free workout tracking in Nexal', text: 'Keep assisted exercise results in workout history with your own clear equipment reference.' },
    sources: [{ href: 'https://www.lifefitness.com/en-gb/catalog/cybex/cybex-products/cybex-dip-chin-assist', label: 'Cybex: Dip/Chin Assist assistance and hand positions' }],
    faqs: [
      { question: 'Is a higher assistance number a heavier pull-up?', answer: 'On a machine where the setting increases assistance, it means more help. Confirm the behaviour of your particular model.' },
      { question: 'Can I combine band and machine-assisted records?', answer: 'Keep them distinguishable. A band description is not a verified equivalent to a machine assistance setting.' }
    ]
  },
  {
    slug: 'log-alternating-reps-unfinished-side-sequences',
    title: 'Logging alternating repetitions and unfinished side sequences',
    metaTitle: 'Log Alternating Reps and Unfinished Sequences',
    description: 'Audit alternating movements, distinguish individual reps from pairs and preserve unfinished sequences with a worked logging example.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'An alternating exercise can stop between two sides. If you remember five complete left-right pairs and one final left movement, writing six pairs invents a right movement that never happened. A short sequence audit preserves that unfinished ending without turning the diary into an exercise prescription.',
    takeaway: 'Confirm whether the programme counts individual movements or pairs. Record the completed sequence, its stopping point and any later continuation without rounding an unfinished pair into a complete one.',
    sections: [
      { id: 'count-definition', title: 'Clarify the counting unit before tracing the sequence', paragraphs: [
        'Ask the programme author what one counted repetition represents for this particular exercise. An individual movement and a left-right pair are different units. NHS strength examples identify work on particular sides, but a general example cannot resolve the wording of your personal programme. Keep the clarified definition beside the exercise name in your own reference.',
        'Also establish whether alternating means changing sides after every movement or completing a block before switching. Those instructions produce different sequences. Do not reinterpret an unclear target by doing extra work. This article explains how to record an existing suitable routine after its instructions have been clarified, not how to choose its order or workload.'
      ] },
      { id: 'sequence-audit', title: 'Audit an ending that falls between the sides', paragraphs: [
        'Consider an explicitly illustrative completed sequence: L-R, L-R, L-R, L-R, L-R, L. It contains eleven individual movements: six left and five right. Under a pair-counting convention, it contains five complete pairs and one unpaired left movement. Neither twelve individual movements nor six complete pairs describes what happened. These invented counts explain notation, not a training target.',
        'Keep the sequence summary in a companion note if the tracker cannot express an unfinished pair. Use a supported entry only when its unit remains truthful. A single field reading eleven is understandable if it explicitly means individual movements; it is misleading if a later reader assumes eleven pairs. Preserve the counting key rather than expecting a total to explain itself.'
      ] },
      { id: 'continuation-boundary', title: 'Keep a later continuation visibly separate', paragraphs: [
        'Suppose the illustrative sequence stops, and a later instructed continuation contains one right movement. You now have twelve individual movements across two efforts, but not six uninterrupted pairs. Record the break and the continuation separately in your reference. Whether your programme treats them as the same set is a question for its author, not something the diary total can decide.',
        'Do not fill in a missing side from an intended target or from the fact that the other side appears in history. If you cannot remember the ending, describe it as uncertain rather than constructing a balanced sequence. A diary records completed work; it does not create an obligation to finish an omitted movement or compensate during another session.'
      ] },
      { id: 'reconciliation', title: 'Reconcile the side counts with the sequence total', paragraphs: [
        'Use a three-part check after logging: add the completed left and right movements, compare that sum with the individual-movement total, and explain any unfinished pair. In the example, six plus five equals eleven. This arithmetic checks the record, not movement quality, physical symmetry or the suitability of a future programme.',
        'Keep a dated change note if you switch from individual movements to pair notation. Earlier totals should retain their original meaning. An odd individual count can indicate an unfinished alternating sequence, but it does not prove which side ended the effort. Preserve the actual order when known and do not reconstruct it from arithmetic alone.'
      ] },
      { id: 'workflow', title: 'Test the counting key alongside Nexal workout history', paragraphs: [
        'Download Nexal on Android and use free manual workout logging and custom workouts for a familiar suitable session. Keep sequence details in your own notes wherever the supported fields cannot represent them. Reopen history and check whether you can explain the counting unit and any unfinished ending without relying on memory.',
        'Premium AI planning is optional and separate from this recording task. No automatic alternating-side detection or sequence calculator is promised here. The useful test is whether your saved workout and companion counting key preserve what happened clearly enough for a later review or a question to your trainer.'
      ] }
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore Nexal’s free workout records', text: 'Test manual workout logging and history with your existing routine on Android.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/strength-exercises/', label: 'NHS: strength examples with per-side movements' }],
    faqs: [
      { question: 'Are eleven alternating movements six pairs?', answer: 'No. A sequence of five complete left-right pairs followed by one left movement contains eleven individual movements and one unfinished pair.' },
      { question: 'Should I perform an extra movement to balance the diary?', answer: 'A recording mismatch is not an instruction to do additional exercise. Record what happened and clarify any programme question with its author.' }
    ]
  },
  {
    slug: 'record-machine-seat-settings',
    title: 'Recording machine seat settings for comparable sessions',
    metaTitle: 'Record Gym Machine Seat Settings Clearly',
    description: 'Build a machine setup reference with seat, support and start-position settings while keeping equipment labels separate from load.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'You remember the weight from your last machine session but cannot remember where the seat was. That missing setup can make the next entry hard to interpret. A compact machine reference helps you reproduce the instructed configuration and recognise when a session used something different.',
    takeaway: 'Tie settings to a specific machine, distinguish each adjustment, and date meaningful changes. A remembered seat number is a memory aid rather than proof of a suitable setup.',
    sections: [
      { id: 'identify-station', title: 'Attach the setting to the actual station', paragraphs: [
        'Record the manufacturer, model or gym equipment identifier before recording seat three. Two machines with similar upholstery may have different adjustment systems. Life Fitness describes different seat and start-position mechanisms within its Hammer Strength Select line, so even a familiar brand is not enough to establish identical settings.',
        'Choose an identifier you can recognise after the gym rearranges the room. Row machine near window is a fragile reference; the facility’s asset label or model name is more durable. Ask staff to identify an unlabelled station rather than guessing its model from appearance.'
      ] },
      { id: 'separate-adjustments', title: 'Give every adjustment its own name', paragraphs: [
        'Seat height, back support, chest support and start position are separate details. Preserve the printed number or letter beside the correct adjustment. If two dials both show four, a note saying machine four cannot tell you which one was changed. Keep the reference compact but specific.',
        'Where markings are absent, ask an instructor for a reliable way to recognise the setup they taught you. Do not improvise a body-position rule from a generic article. The log records an explained configuration; the machine instructions and qualified guidance determine how you should use it.'
      ] },
      { id: 'setup-card', title: 'Build an equipment card instead of a long diary entry', paragraphs: [
        'An illustrative companion card might read: chest press CP-2; seat B; start lever 3; trainer checked 11 October. A session note can then reference CP-2 setup A rather than repeating the whole description. These invented labels show an organisation method and are not universal machine settings.',
        'Keep any changes dated on the same card. If an instructor changes the seat to C, label the revised configuration setup B and record the first session that used it. That lets later readers distinguish a resistance change from a setup change without reconstructing several visits from memory.'
      ] },
      { id: 'verify-before-use', title: 'Check the current setup before comparing results', paragraphs: [
        'Another member may have changed the machine since your last visit. Consult your reference and follow the equipment adjustment instructions before using it. A saved setting does not mean the station is currently configured that way, and a log cannot verify that an adjustment mechanism is correctly engaged.',
        'For comparison, check machine identity, adjustment version, resistance unit and completed repetitions. If a dial marking is unreadable or the machine has been serviced, note the uncertainty and ask staff. Do not force an old number onto equipment that now behaves or labels its settings differently.'
      ] },
      { id: 'app-reference', title: 'Pair free workout history with your setup card', paragraphs: [
        'Nexal provides free manual workout logging, custom workouts and history on Android. Use those features for performed results and keep the equipment card in your own notes if the app fields do not capture each adjustment. Dedicated seat-setting storage is not assumed by this workflow.',
        'Install Nexal and test one machine session by finding both its saved result and the matching setup reference before your next visit. Premium AI workout planning is optional. The immediate benefit to assess is whether you can identify the setup associated with the result without relying on last week’s memory.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore workout history in Nexal', text: 'Use free session records alongside your own equipment setup reference.' },
    sources: [{ href: 'https://www.lifefitness.com/en-us/catalog/strength-training/selectorized/hammer-strength-select', label: 'Life Fitness: seat and start-position adjustment systems' }],
    faqs: [
      { question: 'Can I reuse seat three on every chest press?', answer: 'No. Settings belong to the specific machine and instructed setup. Identical numbers do not establish identical positions.' },
      { question: 'Does Nexal have a dedicated machine setup card?', answer: 'This guide uses your own companion note for setup details and Nexal’s supported free logging tools for workout results.' }
    ]
  },
  {
    slug: 'track-timed-holds-without-rep-totals',
    title: 'Tracking timed holds without inventing repetition totals',
    metaTitle: 'Track Timed Holds Without Inventing Reps',
    description: 'Keep hold durations, interruptions and position changes clear without turning seconds into repetition counts in workout history.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A timed hold does not become thirty repetitions because it lasted thirty seconds. When a workout tracker is organised around sets and reps, squeezing a duration into the wrong field creates false totals. Keep the measurement honest, even if part of the record needs a companion note.',
    takeaway: 'Use seconds for duration, distinguish each uninterrupted attempt, and describe the held position. Never invent a repetition equivalent merely to fill a required field.',
    sections: [
      { id: 'measurement', title: 'Identify the measurement before opening the log', paragraphs: [
        'Check whether your programme asks for a sustained hold, repeated movements with a pause, or several separate holds. Those are different records. The NSCA distinguishes static and dynamic core exercise, providing context for treating a held position differently from a sequence of movements. Follow the exercise definition you were actually given.',
        'For a timed attempt, record duration in a unit such as seconds. For a moving exercise with a pause, keep repetitions and the instructed pause context distinct. A notation like ten with a pause should not silently become a ten-second hold when you review it later.'
      ] },
      { id: 'attempt-boundaries', title: 'Choose a consistent start and stop rule', paragraphs: [
        'Use the timing rule explained in your existing routine, including when the attempt begins and what marks its end. Keep your timing method consistent and place the device safely. This article does not prescribe a position or a duration, and a phone timer cannot check whether you maintained the instructed setup.',
        'If you are estimating after the event, label the duration estimated. A precise-looking number is not automatically a precise measurement. Decide with your instructor how to document an interrupted attempt, then apply that rule without changing it to make one session appear better.'
      ] },
      { id: 'interruption-example', title: 'Separate an interrupted hold from a continuous one', paragraphs: [
        'Suppose an illustrative attempt lasts 18 seconds, stops, and is followed later by 12 seconds. You can record two attempts totalling 30 seconds of observed holding time. You cannot describe that as one uninterrupted 30-second hold. These invented durations explain record boundaries rather than recommend a target.',
        'Keep the interruption outside the holding duration. If your timer continued through the break, preserve what you actually know instead of using the full elapsed reading as hold time. Record an uncertain duration as uncertain and use a clearer timing method next visit.'
      ] },
      { id: 'variation-check', title: 'Match position and support before comparing seconds', paragraphs: [
        'A longer duration in a different position describes a different attempt. Record the variation, support and any externally added resistance when relevant. Avoid assigning bodyweight kilograms to a hold unless your programme and logging method explicitly establish what that figure means.',
        'Before comparing two entries, check the held variation, timing rule, interruption status and whether the duration was measured or estimated. That checklist explains the record without turning the longest hold into an automatic instruction to continue longer next time. Ask your trainer about appropriate progression.'
      ] },
      { id: 'supported-fields', title: 'Test whether the app can represent the result honestly', paragraphs: [
        'Nexal offers free manual workout logging, custom workouts and history on Android. Inspect the available fields before choosing how to record a timed exercise. If they cannot express the duration, keep seconds in your own dated companion record rather than entering seconds as reps or assuming a dedicated hold timer exists.',
        'Download Nexal to try recording the rest of your routine and retrieving the matching hold reference. Premium AI workout planning is an optional way to organise sessions, not a requirement for keeping a truthful duration record. Judge the workflow by whether you can reconstruct each attempt later.'
      ] }
    ],
    feature: { href: '/workout-meal-planner-app', label: 'See Nexal’s manual workout tools', text: 'Use free workout history with companion duration records where needed.' },
    sources: [{ href: 'https://dxpprod.nsca.com/education/articles/kinetic-select/incorporate-dynamic-and-static-core-exercises/', label: 'NSCA: static and dynamic core exercise distinctions' }],
    faqs: [
      { question: 'Can I enter thirty seconds as thirty reps?', answer: 'That changes the meaning of the field. Preserve seconds in a supported duration field or your own companion record.' },
      { question: 'Can interrupted attempts be added together?', answer: 'You may calculate total observed holding time, but keep the attempts separate and do not call the sum one continuous hold.' }
    ]
  },
  {
    slug: 'log-circuit-rounds-with-different-stations',
    title: 'Logging circuit rounds when the stations differ',
    metaTitle: 'Log Circuit Rounds with Different Stations',
    description: 'Keep circuit rounds readable when stations use different units, change order or remain unfinished, with a practical station ledger.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'Three circuit rounds does not tell you whether every station happened three times. One station may use repetitions, another time, and another a different exercise after equipment becomes unavailable. A useful circuit log keeps the round structure without flattening all the station results into one misleading total.',
    takeaway: 'Define the circuit version, record station results in their own units and distinguish complete rounds from partial passes. Round count is an index, not a substitute for completed work.',
    sections: [
      { id: 'station-ledger', title: 'Give each station a stable identifier', paragraphs: [
        'List the stations in your established circuit as A, B and C, with the actual exercise variation beside each letter. Record the expected measurement for each: repetitions, seconds or another explicitly supported unit. ACE’s circuit examples use station-based formats, but their particular exercises and timings are not instructions for your routine.',
        'Keep this station ledger in a companion note when the tracker cannot represent circuit relationships. A saved exercise list alone may not show which entries belong to the same pass. Stable station identifiers let you preserve that relationship without assuming a special circuit mode.'
      ] },
      { id: 'round-definition', title: 'Define what counts as a completed round', paragraphs: [
        'Use a plain rule: one completed round contains one completed visit to every station specified in that circuit version. If your instructor uses another definition, write it explicitly. Going past station B without performing it should not leave the history looking as though B was completed.',
        'Separate planned rounds from actual rounds in your working reference. When a circuit ends partway through, describe complete passes plus the stations performed in the final pass. This preserves useful detail without forcing the session into either an entirely complete or entirely missing category.'
      ] },
      { id: 'mixed-units', title: 'Record a mixed-unit pass without adding unlike numbers', paragraphs: [
        'An illustrative pass might contain A with ten repetitions, B with a 25-second hold and C with eight repetitions. Keep those three results beside their station identifiers. Adding them as 43 repetitions would invent a unit conversion and conceal what each station involved.',
        'Imagine the next pass contains A and C only because B is unavailable. The ledger should show B not performed and explain the changed order. These invented counts and durations illustrate bookkeeping, not a proposed circuit. A shorter list of actual results is better evidence than a neat but inaccurate two-round summary.'
      ] },
      { id: 'changed-station', title: 'Mark a replacement as a circuit version change', paragraphs: [
        'If a trainer supplies an alternative station, preserve the replacement exercise and which pass first used it. Call the modified arrangement version B in your companion ledger if that makes the distinction easy to follow. The original and modified circuits need not share identical station totals.',
        'At review, ask whether the same station identities, units, sequence and completion rule were used. Compare a station’s matching results before comparing circuit summaries. A faster finish with one omitted station is an explanation of a changed session, not evidence that the original circuit became faster.'
      ] },
      { id: 'circuit-app-test', title: 'Try station results in free workout history', paragraphs: [
        'Nexal’s Android manual workout logging, custom workouts and history are free. Use them for the exercise entries your fields support, and keep the station-to-round mapping in your own note. Do not assume the app automatically groups mixed units or detects an incomplete circuit.',
        'Install Nexal and test a circuit you already know with a brief ledger. After saving, reconstruct one pass using the history and reference together. Premium AI workout plans are optional if planning is the task you need help with; the first test here is honest station accounting.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal custom workouts', text: 'Keep performed exercise results in free workout history and circuit context in your own ledger.' },
    sources: [{ href: 'https://www.acefitness.org/continuing-education/certified/training-and-performance-special-issue-2016/6177/get-inspired-with-these-4-fun-and-engaging-circuit-training-formats/', label: 'ACE: examples of station-based circuit formats' }],
    faqs: [
      { question: 'Is a partial pass a completed round?', answer: 'Keep the completion rule explicit. Under the all-stations rule used here, record complete rounds plus the stations performed in the partial pass.' },
      { question: 'Can seconds and repetitions share a circuit total?', answer: 'Keep them in their own units. Adding the numbers together does not produce a meaningful repetition count.' }
    ]
  },
  {
    slug: 'manual-treadmill-incline-speed-log',
    title: 'Keeping a manual treadmill incline and speed workout log',
    metaTitle: 'Manual Treadmill Incline and Speed Log',
    description: 'Record treadmill speed, incline, duration and pauses manually without assuming machine syncing or converting incline levels into percentages.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A treadmill visit recorded only as twenty minutes loses the settings that describe the session. If speed or incline changes, the final console reading may not represent the whole workout. A small manual segment record captures the differences without requiring a connection between the machine and your phone.',
    takeaway: 'Preserve speed units, the displayed incline convention and time spent at each setting. Keep machine readings separate from your own calculations and treat pauses explicitly.',
    sections: [
      { id: 'console-units', title: 'Read the console labels before copying numbers', paragraphs: [
        'Check whether speed is displayed in kilometres per hour or miles per hour and whether incline is a percentage or a numbered level. Life Fitness’s SL console guidance identifies speed, incline and time controls, but your specific console instructions remain the reference for what its labels mean.',
        'Record the treadmill model or gym identifier when useful. A note saying speed five, incline four is incomplete without units and label meanings. Do not translate a level into a percentage from appearance alone, and do not assume a second treadmill uses the same display convention.'
      ] },
      { id: 'segments', title: 'Split the record when a setting changes', paragraphs: [
        'Use a companion segment list with elapsed time, speed and incline. Make an entry when the setting changes, using the console reading when you can do so safely. You do not need to operate the phone while moving: preserve a simple summary after stopping according to the machine instructions.',
        'If you cannot remember an exact transition, label that segment approximate. Avoid retrospectively filling every minute with invented precision. A useful record distinguishes what was read from the console, what was recalled and what remains unknown, especially when you want to compare interval patterns.'
      ] },
      { id: 'illustrative-session', title: 'Keep the final setting from becoming the whole workout', paragraphs: [
        'An illustrative log might show five minutes at 4 km/h and 0% incline, then ten minutes at 5 km/h and 3%, then five minutes at 4 km/h and 0%. These invented settings demonstrate a segment ledger and are not a walking programme or an intensity recommendation.',
        'The final display of 4 km/h at 0% does not describe the middle segment. Likewise, the maximum incline does not describe all twenty minutes. Preserve the segment list instead of labelling the whole visit twenty minutes at 3%. Keep any console distance as a separate displayed observation.'
      ] },
      { id: 'pauses', title: 'Distinguish moving time from elapsed time', paragraphs: [
        'Note whether a pause is included in the console’s timer. If the session includes a break, preserve the observed moving segments and the break separately rather than assuming the display excludes it. Ask staff or consult the manual when the pause behaviour is unclear.',
        'For later comparison, check the same units, incline convention, segment durations and pause treatment. Record relevant support changes, such as using the handrails, as context for your trainer. The log does not translate treadmill settings into a personalised exertion assessment or a guaranteed calorie expenditure.'
      ] },
      { id: 'manual-app', title: 'Pair a manual treadmill ledger with Nexal history', paragraphs: [
        'Nexal offers free manual workouts, custom workouts and history on Android. Use the supported workout fields for your session and retain treadmill segments in your own dated note when they do not fit. This workflow uses manual recording and does not assume a treadmill integration or automatic speed capture.',
        'Download Nexal from Google Play and check whether your saved workout plus segment reference is easy to revisit. Premium AI planning is optional. Start with one existing treadmill session and judge the workflow by whether you can explain its settings, rather than by how many console numbers you copy.'
      ] }
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore manual workout history on Android', text: 'Use Nexal’s free workout tools alongside your own treadmill segment notes.' },
    sources: [{ href: 'https://support.lifefitness.com/hc/en-us/articles/42814152246039-Life-Fitness-Atmos-Treadmill-How-to-Use-Your-SL-Console-Guided-Tour', label: 'Life Fitness: SL console speed, incline and time controls' }],
    faqs: [
      { question: 'Does this workflow sync my treadmill with Nexal?', answer: 'No. It uses manual workout recording and a companion segment ledger. A treadmill connection is not assumed.' },
      { question: 'Can I record only the final incline?', answer: 'Only if it describes the session accurately. When incline changes, preserve the segments rather than applying the last setting to the whole visit.' }
    ]
  },
  {
    slug: 'record-bodyweight-exercise-variations',
    title: 'Recording bodyweight exercise variations in a workout log',
    metaTitle: 'Log Bodyweight Exercise Variations Clearly',
    description: 'Distinguish supports, contact points and exercise variations so bodyweight workout history does not combine unlike repetitions.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A bodyweight exercise can change substantially while the weight field stays empty. Wall, elevated-support and floor variations should not disappear into one generic label if you want to understand your history. Record the setup that gives the repetition count its meaning.',
    takeaway: 'Name the performed variation, describe stable supports and keep changes visible. Bodyweight is an exercise context, not permission to invent a fixed external load for every movement.',
    sections: [
      { id: 'variation-identity', title: 'Make the variation part of the exercise identity', paragraphs: [
        'Use the variation you have been taught rather than a broad family name alone. The NHS strength page describes a wall press-up, illustrating a specific setup within a wider exercise family. Your log should distinguish that variation from another support position instead of treating all press-up counts as interchangeable.',
        'A concise name might identify movement, support and version. Keep detailed technique instructions in your programme reference. The exercise name only needs enough information to select the right history; it does not need to reproduce an entire coaching explanation or become an instruction to try an unfamiliar version.'
      ] },
      { id: 'support-reference', title: 'Describe the actual support consistently', paragraphs: [
        'For an established supported variation, identify the support surface and the contact points that matter to the taught setup. Use a stable equipment identifier where available. Bench by door may stop being meaningful when furniture moves, while bench B identifies the intended reference more clearly.',
        'If height is measured, preserve the unit and method in your companion note. If you do not know the height, retain the specific support identity without inventing centimetres. This is a memory record of a suitable setup, not a recommendation to use household furniture or an unverified surface.'
      ] },
      { id: 'switch-example', title: 'Start a new comparison when the variation changes', paragraphs: [
        'Imagine an illustrative history with twelve repetitions using instructed support A, followed next week by eight using instructed support B. The lower count does not by itself show a decline because the setup changed. Keep each result under its own variation and date the transition.',
        'If a session contains both variations, record each separately rather than merging them into twenty identical repetitions. These invented counts and support labels demonstrate classification only. They do not establish that one variation is harder for you or provide a progression order to follow.'
      ] },
      { id: 'load-field', title: 'Avoid assigning an unsupported bodyweight load', paragraphs: [
        'Do not enter your entire body mass as external resistance merely because an exercise is called bodyweight. The relevant support and movement matter, and this guide offers no formula for converting the variation into lifted kilograms. Keep separately measured body mass separate from the exercise’s load convention.',
        'If your routine uses added external resistance, record what that extra load includes and preserve the underlying variation. At comparison time, check support, contact points, movement definition and external-load convention. A shared exercise family name alone is not enough to justify combining the records.'
      ] },
      { id: 'variation-library', title: 'Build a small useful history in Nexal', paragraphs: [
        'Nexal’s free custom workouts, manual workout logging and history on Android can support your established bodyweight routine. Where supported exercise labels cannot express every setup detail, keep a companion variation key linked to the session date. This does not depend on automatic variation recognition.',
        'Install Nexal and record one familiar variation, then check whether you can find its previous matching entry before the next session. Premium AI workout plans are optional planning tools. Test the free recording workflow with variations you already understand instead of choosing new movements just to populate the app.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore home and gym workout records', text: 'Keep bodyweight workout results in Nexal’s free Android history.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/strength-exercises/', label: 'NHS: a specifically described wall press-up variation' }],
    faqs: [
      { question: 'Should every press-up variation share one history?', answer: 'Keep variation and support distinguishable when they change the meaning of the record. Compare matching setups first.' },
      { question: 'Should I enter body mass as the lifted weight?', answer: 'Do not assume whole body mass equals the external load of the movement. Preserve bodyweight context and any actual added resistance separately.' }
    ]
  },
  {
    slug: 'log-plate-loaded-equipment-weight',
    title: 'Logging plate-loaded equipment: total weight versus plates',
    metaTitle: 'Plate-Loaded Equipment: What Weight to Log',
    description: 'Define plates-only, per-arm and total-load conventions without guessing a machine’s starting resistance or mixing storage horns with loading arms.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'The plates you can see on a machine do not automatically tell you which number belongs in your workout log. Independent arms, storage horns and manufacturer starting-resistance figures can create several plausible totals. Choose a convention that describes the equipment instead of importing a barbell rule without checking it.',
    takeaway: 'Identify active loading points, say whether your entry means plates per arm or combined plates, and preserve any verified starting-resistance specification separately.',
    sections: [
      { id: 'loading-points', title: 'Distinguish loading arms from plate storage', paragraphs: [
        'Ask staff which horns load the moving mechanism and which simply store plates. Count only the plates relevant to the performed setup. A storage peg holding several spare plates is not part of your exercise load just because it is attached to the same frame.',
        'Record the machine model and whether its work arms operate independently. Keep the relevant loading arrangement in a companion equipment reference. This guide does not provide loading instructions; follow the manufacturer guidance and the setup you have been taught, including any requirements concerning balanced loading.'
      ] },
      { id: 'number-convention', title: 'Define plates per arm versus combined plates', paragraphs: [
        'For an illustrative machine with two active arms, one 10 kg plate on each arm can be described as 10 kg plates per arm or 20 kg combined plates. Both labels explain the plate inventory. A bare entry of 20 does not explain whether it refers to one side, both sides or a manufacturer resistance figure.',
        'Use the convention expected by the app field where that is clear, and keep its meaning in your reference. Do not change from combined to per-arm notation halfway through a comparison period. These invented quantities demonstrate bookkeeping and are not recommended machine loads.'
      ] },
      { id: 'starting-resistance', title: 'Keep starting resistance tied to documentation', paragraphs: [
        'Some manufacturers publish a starting-resistance figure. The Life Fitness decline chest press specification, for example, labels its starting resistance per workarm. That wording matters: a per-arm figure cannot silently become a whole-machine figure. Check the documentation for your actual equipment before using any such value.',
        'If your chosen convention is plates only, label it plates only and retain a verified starting-resistance specification separately. If your programme uses a documented total convention, record exactly how it is defined. Do not guess a carriage weight or assume the machine behaves like a freely lifted barbell.'
      ] },
      { id: 'audit-example', title: 'Check the inventory before interpreting a jump', paragraphs: [
        'Suppose an old illustrative entry reads 20 kg and the new one reads 40 kg. Before calling that a doubling, check whether the old record meant plates per arm and the new one meant combined plates. A change of notation can produce an apparent jump without any physical change.',
        'Use an audit order: machine identity, active loading points, plate units, per-arm or combined convention, and treatment of starting resistance. If an older value cannot be decoded, leave it ambiguous rather than rewriting it confidently. Start a clearly labelled series from the next actual session.'
      ] },
      { id: 'first-record', title: 'Try one documented convention in Nexal', paragraphs: [
        'Use Nexal’s free manual workout logging, custom workouts and history on Android for the performed results. Keep the machine specification and plate convention in a companion note when the available fields cannot express them. Automatic machine-resistance calculation is not assumed here.',
        'Download Nexal and check a saved plate-loaded session against the equipment reference before using it again. Premium AI workout planning is optional. A successful first test means you can explain what the weight includes, even when that means retaining an explicitly plates-only record instead of an uncertain total.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See Nexal manual workout logging', text: 'Record plate-loaded sessions free with a consistent equipment convention.' },
    sources: [{ href: 'https://www.lifefitness.com/en-eu/catalog/strength-training/plate-loaded/life-fitness-decline-chest-press', label: 'Life Fitness: per-workarm starting-resistance specification' }],
    faqs: [
      { question: 'Must I always add the machine’s starting resistance?', answer: 'Use a clearly defined convention and verified model documentation. A labelled plates-only record is preferable to an invented total.' },
      { question: 'Should storage plates count in my logged load?', answer: 'No. Identify the active loading points with staff and record the plates relevant to the movement, following the equipment instructions.' }
    ]
  },
  {
    slug: 'compare-cable-machines-in-workout-history',
    title: 'Comparing cable machines without assuming identical resistance',
    metaTitle: 'Compare Cable Machines in Workout History',
    description: 'Keep cable station labels, pulley arrangements and attachments distinct instead of treating equal stack numbers as equal resistance.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A cable row labelled twenty on one machine may not mean the same thing as twenty on another. The selector number is useful only with its equipment context. When you change stations, preserve the displayed setting without claiming that the two machines provide identical resistance.',
    takeaway: 'Record the actual station, displayed unit, cable arrangement and attachment. Compare results within a stable setup and use manufacturer documentation to clarify pulley labels.',
    sections: [
      { id: 'stack-meaning', title: 'Find out what the stack label describes', paragraphs: [
        'Read the machine instructions and ask staff whether the displayed quantity refers to a stack setting or a specified output resistance. REP documents a 2:1 pulley ratio for its Arcadia functional trainer, demonstrating that a manufacturer may distinguish stack loading from the resistance description at the cable.',
        'That example does not establish the ratio of your station. Do not halve every cable-machine number or infer a ratio by counting visible pulleys. Keep the label as displayed and attach the verified model information when available. Unknown mechanics should remain unknown in your record.'
      ] },
      { id: 'setup-key', title: 'Build a station key with the attachment', paragraphs: [
        'Record the station identifier, cable outlet position and attachment used for the instructed movement. Include whether the setup uses one cable or a manufacturer-approved arrangement involving more than one. These details belong in a companion equipment key if the supported workout fields do not capture them.',
        'A rope, single handle and bar should not disappear into an identical exercise label when the attachment matters to the taught setup. Ask an instructor how to recognise the intended arrangement rather than improvising combinations to match an old weight. Logging the setup does not validate an unfamiliar configuration.'
      ] },
      { id: 'station-example', title: 'Treat a station change as a new observation', paragraphs: [
        'Consider an illustrative record: station A, displayed 20 kg, ten completed repetitions; station B, displayed 20 kg, eight repetitions. The shared displayed number does not establish equivalent resistance. Record both results under their station contexts rather than calling the second a two-repetition decline under identical conditions.',
        'If documentation later explains the label conventions, preserve that information with the equipment key. Do not retroactively calculate an exact performance equivalence from a ratio alone. These invented figures demonstrate comparison limits and do not suggest which setting to use on either station.'
      ] },
      { id: 'comparison-check', title: 'Use a match checklist before reading progress', paragraphs: [
        'Check station identity, displayed unit, cable arrangement, outlet position, attachment and movement variation. Then compare completed repetitions and the recorded setting. A changed setup should prompt a description of the change before any conclusion about performance. Equal numbers are only one item on that checklist.',
        'If a cable feels unexpectedly different on the same station, record the observation and ask gym staff about the equipment. Do not diagnose friction, wear or a maintenance fault from your diary. Your record can identify when and where a difference was noticed without establishing its cause.'
      ] },
      { id: 'app-workflow', title: 'Keep station context beside free workout records', paragraphs: [
        'Nexal offers free Android manual workout logging, custom workouts and history. Use them for the exercise results and retain your station key in a companion note. This workflow does not assume the app knows pulley ratios, identifies equipment or normalises cable resistance between machines.',
        'Install Nexal and test whether you can find the previous session on the same cable setup. Premium AI workout planning is optional if organising the routine is your next task. For this decision, the useful first outcome is a readable history that does not mistake a new station for a directly comparable old one.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore free workout history in Nexal', text: 'Track real cable exercise results with your own station reference.' },
    sources: [{ href: 'https://repfitness.com/blogs/guides/best-compact-functional-trainer-arcadia', label: 'REP Fitness: an explicitly documented cable pulley ratio' }],
    faqs: [
      { question: 'Are equal cable stack numbers directly comparable?', answer: 'Only after the equipment and label conventions are understood. Keep different stations distinguishable instead of assuming equal output resistance.' },
      { question: 'Does Nexal convert between cable machines?', answer: 'This method uses manual records and manufacturer information. Automatic resistance normalisation is not an assumed feature.' }
    ]
  },
  {
    slug: 'record-range-of-motion-changes',
    title: 'Recording range-of-motion changes in exercise history',
    metaTitle: 'Record Range-of-Motion Changes in Your Log',
    description: 'Keep movement-range versions and changed repetition criteria visible without using a workout log to prescribe or assess exercise range.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A repetition count loses meaning if the movement’s start and finish change without a record. This can happen after instruction, a changed setup or a revised exercise variation. A log should preserve that boundary so a larger count does not hide a different repetition definition.',
    takeaway: 'Describe the range you were taught, date changes and compare matching definitions. A workout record documents performed movement; it cannot establish the range that is appropriate for you.',
    sections: [
      { id: 'range-reference', title: 'Write the instructed endpoints in plain language', paragraphs: [
        'Ask your trainer how to describe the starting and finishing positions for the exercise you use. Keep their explanation in your programme reference, using concrete cues you understand. Avoid vague labels such as full or normal when they do not tell you what happened during the movement.',
        'The NHS strength examples describe movement endpoints for particular exercises, showing how a movement description adds information beyond its name. Those general examples do not set your individual range. This guide does not prescribe joint angles or tell you to extend a movement to improve the appearance of the log.'
      ] },
      { id: 'version-change', title: 'Create a dated range version when instruction changes', paragraphs: [
        'If an instructor revises the movement description, preserve both the earlier and current versions. A companion note could identify range A before the review and range B from the first session using the new instruction. Keep the reason as stated rather than supplying your own explanation.',
        'Associate each completed session with the version actually used. Do not rewrite older entries as though the latest instruction applied then. If the change happened partway through a session, identify which sets used each version so the visit does not become one falsely uniform result.'
      ] },
      { id: 'count-example', title: 'Explain a repetition change without ranking it', paragraphs: [
        'An illustrative history might show ten repetitions under range A and eight under range B at the same displayed load. The count changed while the repetition definition also changed. That supports describing two different conditions, rather than declaring a decline or claiming that the earlier count was invalid.',
        'If you cannot identify which range an older set used, mark that limitation in your companion notes. These invented counts are classification examples, not a recommended training progression. Preserve what was actually recorded and start a clear comparison from the first well-described session.'
      ] },
      { id: 'unexpected-range', title: 'Distinguish planned changes from observations', paragraphs: [
        'An intentionally revised variation differs from noticing that your movement changed during an attempt. Record the observation plainly, including the affected set, without treating it as a new instruction. If discomfort or uncertainty led to the change, seek appropriate guidance rather than interpreting the log as permission to continue.',
        'Use a review checklist: movement version, setup, endpoints, completed repetitions and whether the range change was planned. A diary can support a precise trainer question about what to count next time. It cannot diagnose a restriction or prescribe a corrective exercise.'
      ] },
      { id: 'history-workflow', title: 'Keep range context with your Android workout history', paragraphs: [
        'Nexal’s free manual workouts, custom workouts and history can hold your established routine and results. Keep the range-version reference separately where supported fields do not express the detail. Automatic motion measurement or technique assessment is not part of the workflow described here.',
        'Download Nexal to test finding a completed session alongside the matching instruction version. Premium AI workout planning is optional, and generated text does not observe your movement. The practical test is whether the saved history helps you ask an informed question when the definition of a repetition changes.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore manual workout history and planning', text: 'Keep actual results free and retain your own movement-version reference.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/strength-exercises/', label: 'NHS: exercise descriptions with movement endpoints' }],
    faqs: [
      { question: 'Is a lower count after a range change a decline?', answer: 'The conditions changed. Describe the new repetition definition and compare matching versions before drawing a conclusion.' },
      { question: 'Can the log tell me which range is suitable?', answer: 'No. Use qualified instruction for suitability and keep the log as a record of the movement actually performed.' }
    ]
  },
  {
    slug: 'workout-history-permanent-equipment-change',
    title: 'Keeping workout history when gym equipment changes permanently',
    metaTitle: 'Workout History After Gym Equipment Changes',
    description: 'Preserve retired equipment records, start a clearly labelled replacement history and avoid invented weight conversions after a gym refit.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A gym refit removes the machine you used for months and replaces it with a different model. This is more than a one-day substitution: every future session needs a new equipment reference. Preserve the old history while making the permanent transition easy to recognise.',
    takeaway: 'Record the retirement boundary, identify the replacement and start a new comparison series. Preserve old values under their original conventions rather than converting them to match the new machine.',
    sections: [
      { id: 'close-old-series', title: 'Mark the last actual session on the old equipment', paragraphs: [
        'Find the latest saved workout you can confidently associate with the retired machine. Record its date and identifier in a companion transition note. If you do not know the exact removal date, say last recorded use instead of inventing a precise retirement date for the gym.',
        'Keep earlier entries intact as records of their original conditions. A machine disappearing does not make its history useless, but it does limit direct numerical comparison with the replacement. Retain the old weight convention and setup key so you can still explain those sessions later.'
      ] },
      { id: 'new-equipment', title: 'Build the replacement reference from scratch', paragraphs: [
        'Identify the new manufacturer, model, units and adjustment system. Ask staff for instruction before treating a familiar exercise name as a familiar machine. Life Fitness documents different seat and start-position systems within its equipment range, illustrating why an old setting should not automatically transfer to a replacement.',
        'Record the replacement setup you are taught in a new companion equipment card. Do not copy the old selector value as a starting instruction. This is an administrative handover, not a method for selecting a suitable load or deciding whether the replacement movement belongs in your programme.'
      ] },
      { id: 'transition-example', title: 'Use an explicit boundary instead of a conversion factor', paragraphs: [
        'An illustrative transition note could read: old row R-1, last recorded use 8 October; replacement R-2, first recorded use 11 October. A result of 40 kg on R-1 and 25 kg on R-2 then remains two labelled machine observations. The invented figures are not an equivalence or a suggested replacement load.',
        'Keep a stable movement-family reference if useful, but distinguish the equipment-specific series. Do not multiply all old entries by a guessed factor to make the graph continuous. A visible break is more honest than a seamless line based on an unsupported conversion.'
      ] },
      { id: 'new-baseline', title: 'Review consistency within the replacement series', paragraphs: [
        'Once you have actual sessions on the replacement, compare those with each other under the same setup. Check that the machine identity, load label, adjustment version and exercise definition match. Ask the programme author how they want to interpret the transition rather than demanding a new personal record immediately.',
        'If the replacement changes the movement enough to require a revised programme instruction, preserve that revision separately from the equipment event. The machine change date and the programme revision date may differ. Keeping both prevents a later review from attributing every difference to a single cause.'
      ] },
      { id: 'update-working-copy', title: 'Update the current routine without rewriting past visits', paragraphs: [
        'Nexal provides free custom workouts, manual logging and history on Android. Use the supported workflow to make your current routine reflect the replacement while keeping completed sessions understandable under their original labels. Retain the transition register in your own notes where extra metadata does not fit.',
        'Install Nexal to test finding the last old-equipment result and first new-equipment result. Premium AI workout planning is optional if you need help organising a revised routine. Your immediate recording goal is a traceable handover that preserves past work and makes future comparisons specific to the new station.'
      ] }
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore Nexal custom workouts and history', text: 'Keep current routines and completed results clear through equipment changes.' },
    sources: [{ href: 'https://www.lifefitness.com/en-us/catalog/strength-training/selectorized/hammer-strength-select', label: 'Life Fitness: differences in machine adjustment systems' }],
    faqs: [
      { question: 'Should I delete history for a retired machine?', answer: 'Preserve useful old records with their original equipment context. Mark the transition and compare replacement sessions within their own series.' },
      { question: 'Can I convert every old machine weight to the new one?', answer: 'Do not invent an equivalence. Different equipment and label conventions can prevent a defensible direct conversion.' }
    ]
  },
  {
    slug: 'prepare-trainer-questions-from-workout-log',
    title: 'Using a workout log to prepare questions for a trainer',
    metaTitle: 'Prepare Trainer Questions from Workout Logs',
    description: 'Turn specific session observations into useful trainer questions with dates, context and a clear decision to clarify at your next review.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A trainer review is easier to use when you bring a specific question rather than a month of unexplained numbers. Your workout history can supply the date, exercise and context behind that question. Preparing a short agenda helps you clarify an instruction without asking the log to make the decision itself.',
    takeaway: 'Separate observation, uncertainty and the decision you want clarified. Bring a small number of relevant records and write down the trainer’s answer with its effective date.',
    sections: [
      { id: 'choose-decision', title: 'Start with the decision you need clarified', paragraphs: [
        'Choose a concrete question such as which equipment variation belongs in session B, how the programme counts alternating repetitions, or what to record after an interrupted set. These are easier to answer than a broad request to explain every change in your workout totals.',
        'The NSCA’s client-consultation guidance discusses clarifying questions and agreements between trainer and client. Use that communication principle without turning your diary into an assessment tool. A question about an instruction should identify the instruction, not imply that a larger logged number proves what the next programme should be.'
      ] },
      { id: 'evidence-packet', title: 'Choose the smallest relevant set of records', paragraphs: [
        'Find the actual session dates and exercise entries connected to the uncertainty. Include the programme version, machine or variation and what you completed. Keep unrelated visits out of the immediate agenda so the trainer can see the relevant sequence without reconstructing your entire history.',
        'If a detail is missing, say so. Remembering that something felt different is an observation; identifying the exact cause requires more evidence. A short companion note can distinguish recorded facts from later recollection and prevent an uncertain equipment setting from becoming a confident premise in the conversation.'
      ] },
      { id: 'question-example', title: 'Write observation, context and question as separate lines', paragraphs: [
        'An illustrative agenda item could say: on 8 October I recorded ten repetitions on row R-1; on 11 October I used replacement R-2 and recorded eight; which setup should I use for the next session? The invented dates, labels and counts show question structure, not a performance comparison or suggested workload.',
        'The observation gives evidence, the equipment change supplies context and the final line requests a decision. Avoid leading wording such as why did I get weaker when the conditions differ. Also avoid asking the trainer to infer facts you can clarify directly with gym staff, such as an unreadable machine model.'
      ] },
      { id: 'prioritise-agenda', title: 'Sort questions by what affects your next visit', paragraphs: [
        'Put unclear exercise instructions and unresolved equipment choices before optional analysis of long-term totals. Use a short checklist: what happened, where it is recorded, what remains unknown and what answer would change your next action. This creates a practical agenda rather than an argument for a predetermined progression.',
        'Keep symptoms or concerns in plain language and seek appropriate professional advice when needed. The workout log cannot diagnose a problem, and the trainer discussion should not be treated as a substitute for an assessment outside their scope. Do not continue an uncertain movement merely to collect another data point.'
      ] },
      { id: 'capture-answer', title: 'Use free history as a personal conversation reference', paragraphs: [
        'Nexal’s Android manual workout logging, custom workouts and history are free. Open the relevant entries during your review or manually summarise them in your own agenda. This method does not assume a coach portal, export feature or automatic sharing with a trainer.',
        'After the conversation, record the answer and the date it applies from in your companion programme notes. Download Nexal to test whether retrieving those actual session records makes your next review easier to prepare. Premium AI workout planning is optional and does not replace clarification from the person who supplied your instructions.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore free workout history for personal review', text: 'Find completed sessions in Nexal and prepare your own trainer discussion notes.' },
    sources: [{ href: 'https://dxpprod.nsca.com/education/articles/kinetic-select/client-consultation/', label: 'NSCA: clarifying questions in a client consultation' }],
    faqs: [
      { question: 'Do I need to show every workout to my trainer?', answer: 'Choose records relevant to the question and retain access to the rest. A focused agenda helps establish the context quickly.' },
      { question: 'Does Nexal send my history to a trainer?', answer: 'This guide uses personal history review and your own manual summary. It does not assume automatic sharing or a coach account.' }
    ]
  },
  {
    slug: 'log-partially-completed-sets',
    title: 'Logging partially completed sets without inflating totals',
    metaTitle: 'Log Partially Completed Sets Accurately',
    description: 'Separate planned reps, completed reps, incomplete attempts and later continuations so an interrupted set remains truthful in workout history.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'An intended set can end before the planned count, or the final attempted repetition may not meet the movement definition you were taught. Leaving the target in the completed field inflates the record. Keep actual repetitions, unfinished attempts and any later continuation distinguishable.',
    takeaway: 'Record completed repetitions according to the agreed movement definition. Preserve attempts and interruptions as context, and do not treat a resumed effort as automatically continuous.',
    sections: [
      { id: 'completion-rule', title: 'Know what your programme counts as a repetition', paragraphs: [
        'The NHS describes a repetition as one complete movement. For your exercise, use the definition supplied by your trainer or programme, including any relevant range instruction. If you are unsure whether an attempt meets that definition, retain the uncertainty instead of rounding it into a completed repetition.',
        'Do not use a tracking goal as a reason to continue an attempt or change technique. This guide concerns bookkeeping after the work, not advice about training to failure. Keep the intended count in the planning reference and the observed result in the performed record.'
      ] },
      { id: 'short-set', title: 'Replace the target with the actual completed count', paragraphs: [
        'Imagine an illustrative target of ten repetitions, with seven completed and one further attempt left incomplete. The completed count is seven under the agreed definition. The unfinished attempt can be mentioned in your companion note, but it should not turn the result into eight or preserve the original ten.',
        'These invented counts demonstrate record accuracy rather than prescribe a set. If the app initially shows planned values, review them before saving so the history represents the result. A set that ends early is still a performed set with an actual count; it does not have to disappear entirely.'
      ] },
      { id: 'continuation', title: 'Keep a resumed effort visibly interrupted', paragraphs: [
        'Suppose you complete seven repetitions, stop, and later complete three more. You may describe ten completed repetitions across two efforts, but that differs from one uninterrupted set of ten. Use the set boundaries agreed with your trainer and retain the break in your own session context.',
        'Do not automatically merge the continuation with the first effort because they used the same equipment. Likewise, avoid counting the original planned ten plus the later three. That would include work that never happened. The diary needs the actual sequence before anyone can interpret its training meaning.'
      ] },
      { id: 'session-audit', title: 'Check unfinished exercises without making zeroes ambiguous', paragraphs: [
        'Distinguish an exercise not started from a set started with no completed repetitions. A missing entry may represent either, so keep the status explicit in your companion notes when the app fields cannot express it. Do not add a fictional completed set merely to explain why a planned line remained unused.',
        'At the end of the visit, check planned versus completed counts, duplicate continuations and whether an incomplete attempt was accidentally included. Keep reasons factual, such as an appointment ending the session or an equipment interruption. A diary can document the event without diagnosing why an attempt stopped.'
      ] },
      { id: 'free-workflow', title: 'Test honest set entry in Nexal before adding planning tools', paragraphs: [
        'Nexal includes free manual workout logging, custom workouts and history on Android. Record the completed counts using supported fields and keep interruption details in your own dated reference where needed. This workflow does not assume automatic detection of missed repetitions or partial attempts.',
        'Install Nexal and reopen one saved session to check that targets have not been mistaken for results. Premium AI workout planning is optional if you need help organising future sessions. The useful first outcome is a record that remains accurate even when a visit differs from the plan.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore truthful manual workout tracking', text: 'Keep completed set results in Nexal’s free Android history.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/how-to-improve-strength-flexibility/', label: 'NHS: definitions of repetitions and sets' }],
    faqs: [
      { question: 'Should an incomplete attempt count as a full rep?', answer: 'Use the movement definition you were taught. Preserve incomplete or uncertain attempts as context rather than automatically counting them as completed.' },
      { question: 'Can I combine reps completed before and after a break?', answer: 'You can calculate a total, but keep the interruption and effort boundaries visible. Do not describe the total as automatically one continuous set.' }
    ]
  },
  {
    slug: 'organise-searchable-exercise-names',
    title: 'Organising exercise names so workout history stays searchable',
    metaTitle: 'Organise Searchable Exercise Names',
    description: 'Create a stable exercise naming key, distinguish meaningful variations and handle old aliases without assuming automatic history merging.',
    publishedAt: '2026-10-11', contentBatch: 'expansion-2', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'Your history may contain cable row, seated row and row machine even when you meant the same station. It may also contain genuinely different exercises under one generic row label. A small naming key helps you find matching records without merging distinctions that explain the workout.',
    takeaway: 'Use a stable movement name with meaningful variation and equipment context. Keep aliases in a companion index and test retrieval before changing labels across a whole routine.',
    sections: [
      { id: 'naming-key', title: 'Choose a short name structure you can repeat', paragraphs: [
        'Start with movement, then the variation or support, then an equipment identifier only when it matters. For example, seated cable row R-2 is more useful than today’s row. Keep the name short enough to recognise during a session and put detailed instructions in the programme reference.',
        'Official equipment pages use specific movement names and model identifiers, such as Precor’s Discovery chest press. That offers a useful source for identifying a machine, but your naming key should also preserve the actual variation you performed. A manufacturer model is context rather than a substitute for the exercise description.'
      ] },
      { id: 'identity-vs-context', title: 'Separate stable identity from temporary session detail', paragraphs: [
        'Resistance, completed repetitions and visit dates belong in the performed record rather than becoming new exercise names every time. Row 30 kg Monday will fragment the history as soon as either detail changes. Keep the stable name and record changing results in their supported fields.',
        'By contrast, a different support, attachment or machine may deserve a distinct identity when it changes the comparison. Ask whether combining two labels would hide a meaningful condition. Use that question to decide which details belong in the name and which can stay in a companion equipment key.'
      ] },
      { id: 'alias-register', title: 'Keep old aliases without inventing equivalence', paragraphs: [
        'An illustrative companion index could say: seated cable row R-2; earlier alias cable row from sessions checked individually. That last qualification matters. Do not declare every historical entry called cable row equivalent when you cannot establish which machine or variation it described.',
        'For ambiguous old names, preserve the original and mark context unknown. For known duplicates, keep an alias reference so you can consult both labels manually. This method does not assume the app can rename, merge or relink all historical exercise records automatically, and it does not require rewriting uncertain history.'
      ] },
      { id: 'retrieval-test', title: 'Test three retrieval tasks before expanding the key', paragraphs: [
        'Choose a familiar movement and try finding its previous matching session, distinguishing its alternative variation and locating a record under an old alias. Use whichever supported navigation or search tools are available. The goal is reliable retrieval, not a promise that every app has a dedicated full-text exercise search.',
        'If two names still look identical in the available display, revise the key for future entries and date the change. Keep a small list of canonical names in your own notes rather than inventing a new abbreviation at each visit. Avoid punctuation-heavy labels that you cannot remember consistently.'
      ] },
      { id: 'app-trial', title: 'Build your first clear exercise history in Nexal', paragraphs: [
        'Nexal provides free custom workouts, manual workout logging and history on Android. Check its supported exercise choices and labels before applying your naming key. Where custom naming or extra context is not available, retain the mapping in a companion index rather than assuming a feature exists.',
        'Download Nexal and test finding one real previous session using that index. Premium AI workout planning is optional when you need help creating a routine; it is not necessary simply to keep your existing exercise names understandable. A maintainable naming habit is the practical result to assess during the free logging trial.'
      ] }
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore free custom workouts and history', text: 'Try Nexal on Android with a small, consistent exercise naming key.' },
    sources: [{ href: 'https://www.precor.com/en-US/products/DPL0540', label: 'Precor: a named exercise and model-specific equipment reference' }],
    faqs: [
      { question: 'Should I put the session date in every exercise name?', answer: 'Keep the exercise identity stable and record the visit date separately. Changing the name each visit makes matching history harder to retrieve.' },
      { question: 'Can Nexal merge all my old exercise aliases?', answer: 'Automatic historical merging is not assumed here. Keep a companion alias index and use the supported history navigation.' }
    ]
  }
];

export const trainingDetailGuides: Guide[] = articles;
