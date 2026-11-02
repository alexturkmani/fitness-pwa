import type { Guide } from './content';

export const trainingGuides: Guide[] = [
  {
    slug: 'two-day-gym-schedule-tracking',
    title: 'How to track a two-day gym schedule around a busy week',
    metaTitle: 'Two-Day Gym Schedule: Tracking Guide',
    description: 'Organise two gym visits, separate planned and completed sessions, and review a realistic fortnight with free workout logging on Android.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'When you have two reliable gym windows, the useful planning question is how to keep both visits clear and repeatable. This guide is for people with an established, suitable routine who need a calendar and logging method rather than another exercise prescription.',
    takeaway: 'Give each session a stable identity, record the actual visit date, and review two weeks of attendance before changing a schedule that may simply need better time slots.',
    sections: [
      { id: 'choose-windows', title: 'Choose two real windows, including travel time', paragraphs: [
        'Write down when you can leave, when you can reach the gym and when you must be home. A Tuesday slot from 6 to 7 pm is not a full hour of training if getting there takes fifteen minutes. Include changing and returning equipment in the same calculation. Choose windows that work in an ordinary week, not only during a quiet holiday.',
        'For example, someone with evening childcare might reserve Tuesday before work and Saturday after breakfast. Those are calendar examples, not a recommendation about recovery or exercise frequency. Have the content and spacing of your routine checked by the person who designed it when availability changes.'
      ] },
      { id: 'session-identities', title: 'Separate the session name from its weekday', paragraphs: [
        'Use labels such as Session A and Session B for your existing workouts. Calling a workout Tuesday becomes confusing when the appointment moves to Wednesday. The session identity should tell you which exercise list you are following; the date should tell you when you actually performed it.',
        'Keep a separate calendar appointment for each intended visit. Nexal custom workouts can hold your routine, and workout history can hold completed sessions. Do not assume the tracker reserves calendar time or reschedules sessions automatically. A simple calendar reminder outside the app is enough to keep the appointment visible.'
      ] },
      { id: 'fortnight-example', title: 'Read a fortnight without rewriting the routine', paragraphs: [
        'Imagine week one contains A on Tuesday and B on Saturday. In week two, Tuesday becomes unavailable and A happens on Wednesday instead. Recording Wednesday as the actual date preserves a truthful history while leaving the session identity intact. The calendar changed; the completed exercise list did not become a new programme.',
        'At the end of the fortnight, count completed visits and look at which appointments moved. Three moves from the same early slot suggest a scheduling problem worth addressing. They do not tell you to increase training volume. Keep that distinction clear when deciding what to discuss with a trainer.'
      ], example: { title: 'Illustrative two-week attendance record', headers: ['Visit', 'Planned', 'Actual'], rows: [['Week 1 A', 'Tuesday', 'Tuesday'], ['Week 1 B', 'Saturday', 'Saturday'], ['Week 2 A', 'Tuesday', 'Wednesday'], ['Week 2 B', 'Saturday', 'Saturday']], caption: 'A recording example using an existing suitable routine, not a prescribed training timetable.' } },
      { id: 'actual-work', title: 'Record the work completed inside each visit', paragraphs: [
        'Attendance alone cannot explain the session. Log the exercises, completed sets, repetitions and resistance where relevant. If a visit ends early, record the work that happened rather than leaving the planned list looking complete. Keep any explanation, such as an appointment running late, in your own companion note if the available fields do not capture it.',
        'Before repeating A, inspect the previous A entry rather than the most recent workout of any kind. This prevents a Saturday exercise variation from becoming an accidental Tuesday target. Use the earlier record to remember the setup, not as an automatic instruction to lift more.'
      ] },
      { id: 'simple-review', title: 'Try the recording workflow before buying planning help', paragraphs: [
        'Nexal on Android includes core manual workout logging, custom workouts and history for free. Start by recording one real A session and checking that the entry is useful when A comes around again. A clear naming convention often solves more of the two-day scheduling problem than adding another planning tool.',
        'Premium AI workout planning is optional if you need help creating a routine. If your programme already fits, review your two calendar windows first. Change one practical constraint at a time, such as the departure time, so the next fortnight tells you whether that change actually helped attendance.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal workout tracking and planning', text: 'Keep two weekly sessions in free workout history on Android; AI planning is optional Premium functionality.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: free workout tracking and Premium AI planning' }],
    faqs: [
      { question: 'Should I rename a workout when its weekday changes?', answer: 'Keep a stable session name and record the actual date. That lets you find comparable sessions even when appointments move.' },
      { question: 'Does Nexal automatically rearrange my two-day schedule?', answer: 'This guide uses a manual calendar and logging workflow. Automatic rescheduling is not a verified Nexal feature.' }
    ]
  },
  {
    slug: 'four-day-workout-routine-organisation',
    title: 'Organising a four-day workout routine without losing your place',
    metaTitle: 'Organise a Four-Day Workout Routine',
    description: 'Keep four distinct gym sessions organised with clear names, programme versions and a practical weekly review in your workout log.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A four-day routine can become an administration problem when similar sessions blur together. If your programme already suits you, the next task is to preserve its sequence, distinguish repeated exercises and find the right previous record before each visit.',
    takeaway: 'Use four recognisable session names and a programme version. Compare matching sessions, and keep planned appointments separate from completed workout history.',
    sections: [
      { id: 'name-four-sessions', title: 'Give all four sessions an unambiguous name', paragraphs: [
        'Start with the programme you actually intend to follow. If it contains two upper-body and two lower-body sessions, labels such as Upper A, Lower A, Upper B and Lower B are more informative than Workout 1 repeated four times. The labels describe an illustrative organisation method, not a recommendation to choose that split.',
        'Avoid encoding the weekday into the only name. A Thursday workout performed on Friday should still be recognisable. Add a short version identifier when the programme changes, such as Block 2 Upper A, so that old and new exercise lists do not silently share an identical title.'
      ] },
      { id: 'map-sequence', title: 'Map the sequence before filling the calendar', paragraphs: [
        'Put the four session identities on paper first, then assign suitable appointments according to your existing programme. This separates the order of training from the availability of a particular Monday. If you are unsure whether moving sessions changes appropriate recovery, ask the programme author rather than deciding from an empty calendar square.',
        'For someone whose approved sequence is A, B, C, D, a calendar might show A on Monday, B on Tuesday, C on Thursday and D on Saturday. If Saturday is cancelled, keep D visibly unresolved in the planning record. Do not record it as complete just to close the week.'
      ] },
      { id: 'matching-history', title: 'Find the previous matching exercise context', paragraphs: [
        'The same exercise can appear in more than one session with different instructions. A row early in Upper A and a row late in Upper B may follow different preceding work. Record the performed variation and use the relevant session history when reviewing it. Matching a name alone may not be enough.',
        'Suppose Monday includes a seated machine row and Thursday includes a chest-supported dumbbell row. Keep separate exercise identities even if both are informally called rows. A heavier machine label does not establish better performance than a smaller dumbbell number. The equipment and setup are part of the record you are comparing.'
      ], bullets: ['Check the session identity before opening the previous entry.', 'Match exercise variation, equipment and units.', 'Keep the intended instructions available separately from completed results.'] },
      { id: 'version-boundary', title: 'Mark the boundary when a block changes', paragraphs: [
        'When a coach changes the programme, write down the effective date and what changed. A new exercise order, replacement movement or different set instruction should not overwrite your understanding of the previous block. Preserve the earlier history as evidence of what actually happened under those earlier conditions.',
        'Use a small change register outside the app if necessary: Block 2 starts 19 October; Upper B uses a different press variation. That short record is easier to review than reconstructing a month from memory. It also gives your coach a precise question when you are unsure which version to follow.'
      ] },
      { id: 'weekly-audit', title: 'Audit the four records instead of chasing a total', paragraphs: [
        'At the weekly review, look for missing sessions, unfinished entries and accidental duplicate logs. Four saved entries do not necessarily mean all four intended sessions happened. Two may describe the same visit, or one may contain planned values that were never corrected. Resolve those recording errors before judging the routine.',
        'Nexal offers free custom workouts, manual logging and history on Android. Use those tools for the four session lists and completed records. Premium AI planning may help if you need a new structure, but it is not required to organise an existing routine or maintain truthful results across programme versions.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See Nexal workout logging options', text: 'Organise your own four-session routine with free custom workouts and history.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: workout logging and optional AI planning' }],
    faqs: [
      { question: 'Is a four-day split necessary to use a workout tracker?', answer: 'No. This organisation method is for an existing four-session routine, not a recommendation about how often you should train.' },
      { question: 'How should I label two similar sessions?', answer: 'Give each a stable identifier, such as Upper A and Upper B, and include the programme version when their contents change.' }
    ]
  },
  {
    slug: 'home-dumbbell-workout-log',
    title: 'Keeping a useful dumbbell workout log at home',
    metaTitle: 'Home Dumbbell Workout Log: What to Record',
    description: 'Record per-hand weight, adjustable dumbbell settings and left-right repetitions so your home workout history remains understandable.',
    publishedAt: '2026-10-11', category: 'HOME WORKOUTS', readTime: '5 min read',
    intro: 'A home dumbbell log needs to explain what the number on the screen means. This is especially useful when you share adjustable dumbbells, use one weight for some movements and two for others, or have limited equipment increments.',
    takeaway: 'Record the weight convention, exercise variation and completed repetitions consistently. Equipment settings and one-sided work need enough context to make the next comparison fair.',
    sections: [
      { id: 'per-hand-convention', title: 'Decide whether the weight means one dumbbell', paragraphs: [
        'A pair of dumbbells marked 8 kg can be written as 8 kg per hand or 16 kg combined, but switching between those conventions creates a misleading apparent jump. Choose one convention for each exercise and keep it visible in your reference record. Check what a logging field expects rather than assuming every app uses the same interpretation.',
        'For a one-dumbbell movement, record that you used one implement. An entry reading 8 kg for a two-handed hold describes a different setup from 8 kg in each hand. The goal is that you can reconstruct the equipment next time without guessing from the resistance number alone.'
      ] },
      { id: 'adjustable-equipment', title: 'Keep adjustable settings separate from resistance', paragraphs: [
        'If your dumbbells use a selector or removable plates, consult the manufacturer instructions to understand the marked load. Do not assume the handle is included or excluded. Write the model and setting in a companion equipment record when the markings are unclear, and avoid turning an unexplained selector number into kilograms.',
        'For example, household dumbbell A might show a total marked setting of 10 kg, while an older plate-loaded handle needs a separate handle specification. Record each system consistently. When you switch equipment, keep the transition visible instead of treating two identical-looking numbers as proof of identical loading.'
      ] },
      { id: 'sides-and-sets', title: 'Make one-sided work readable', paragraphs: [
        'If your suitable routine includes one-sided exercises, note whether a repetition count applies to each side or both sides together. Left 10, right 10 communicates more than 20 when the original instructions use repetitions per side. It also prevents a future entry of 10 from appearing to halve the work.',
        'Consider an illustrative session with left 9 and right 8 completed repetitions. Record both results rather than rounding them into two sets of 10. Unequal entries are a reason to preserve detail and ask an instructor if needed, not a basis for diagnosing an imbalance or prescribing extra work to one side.'
      ], example: { title: 'Illustrative notation for a home log', headers: ['Situation', 'Clear record'], rows: [['Two dumbbells', '8 kg per hand; completed reps 10, 9'], ['One dumbbell held with both hands', 'One 8 kg dumbbell; completed reps 10'], ['One-sided exercise', '8 kg; left 9, right 8']], caption: 'Invented recording examples, not recommended exercises, loads or repetition targets.' } },
      { id: 'limited-increments', title: 'Avoid inventing progress between equipment steps', paragraphs: [
        'A home set may offer only a few resistance options. Record the actual marked weight even when it stays unchanged for several sessions. The record can still show completed repetitions, the same variation becoming more familiar, or fewer interruptions. Those observations are more honest than entering a resistance you do not own.',
        'If you change the movement, support surface or how you hold the equipment, give the new variation its own context. Discuss appropriate progression with a qualified trainer rather than compensating for a large equipment jump by improvising a harder movement. A log documents decisions; it does not validate them.'
      ] },
      { id: 'home-log-workflow', title: 'Build a record you can use in your own room', paragraphs: [
        'Before starting, put the exercise list and equipment convention somewhere easy to check. After each completed set, enter the actual result while it is fresh. Once finished, review the saved workout against the dumbbells you used, particularly if another household member changed the settings between exercises.',
        'Nexal provides free custom workouts, core manual workout logging and history on Android. You can use them to record an established home routine. Keep extra equipment details in a separate note if the available fields do not suit them. Premium AI workout generation is optional, and any suggested equipment still needs checking against what you own.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore home workout tracking with Nexal', text: 'Log your dumbbell sessions free on Android and review completed workout history.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: home planning and free workout tracking' }],
    faqs: [
      { question: 'Should I log one dumbbell or the combined pair weight?', answer: 'Use the convention expected by the field, and preserve whether it means per hand or combined. Consistency matters more than choosing a universal convention.' },
      { question: 'What if my dumbbells have no smaller weight increment?', answer: 'Record the equipment you actually use. Ask a qualified trainer about suitable progression rather than inventing intermediate loads in the log.' }
    ]
  },
  {
    slug: 'organise-resistance-band-sessions',
    title: 'How to organise resistance band sessions and track the setup',
    metaTitle: 'Resistance Band Sessions: Logging Guide',
    description: 'Identify your bands, record anchors and setup changes, and organise resistance band sessions without inventing kilogram equivalents.',
    publishedAt: '2026-10-11', category: 'HOME WORKOUTS', readTime: '5 min read',
    intro: 'Resistance band history becomes confusing when the entire equipment description is red band. This guide helps people with an established band routine preserve the setup details that make a later session recognisable, especially when bands come from different sets.',
    takeaway: 'Identify the actual band and setup. Keep colour, anchor, position and completed work visible rather than treating an unverified resistance label as a dumbbell weight.',
    sections: [
      { id: 'band-inventory', title: 'Give each band a specific identity', paragraphs: [
        'Make a short inventory of the bands you own: manufacturer, product line, band style and printed resistance description, where available. Colour alone is an unreliable record when your household has multiple sets. A blue loop from one set should not silently replace a blue handled tube from another in your workout history.',
        'The NHS includes resistance bands among examples of muscle-strengthening activity. That establishes bands as a recognised equipment category, but it does not establish that every band session or resistance level suits you. Use exercise instructions appropriate to your equipment and seek qualified help when you do not know the setup.'
      ] },
      { id: 'setup-reference', title: 'Write a repeatable setup reference', paragraphs: [
        'Record the relevant anchor location, body position and grip for the movement you have been taught. Keep a simple diagram or written reference outside the tracker if those details do not fit the logging fields. The reference is a memory aid, not an instruction to use an attachment that has not been checked.',
        'For example, an existing routine might distinguish a seated band movement from a standing version. Even with the same band, those should remain distinguishable. Follow the equipment manufacturer guidance for inspection, attachment and use. An app cannot inspect a door, anchor or damaged band on your behalf.'
      ] },
      { id: 'avoid-fake-weight', title: 'Keep resistance labels in their original form', paragraphs: [
        'If packaging gives a resistance range, preserve the wording and the manufacturer context. Do not select one end of that range and enter it as though you lifted a fixed dumbbell of that weight. A range on packaging and a completed set describe different things; combining them can create false precision.',
        'When a workout field does not represent your band accurately, keep the band identity in your companion record and log the supported completed-work details. Avoid entering a made-up kilogram number just to populate every field. A partly qualitative record with clear equipment context is more useful than a confident but invented load.'
      ] },
      { id: 'session-example', title: 'Separate equipment changes from performance changes', paragraphs: [
        'Imagine two entries for the same taught movement: the first uses Band A and the second uses Band B after A is misplaced. Preserve the change explicitly. If you complete more repetitions on the second visit, the history cannot tell you whether that reflects a different band, a different setup or a change in performance.',
        'Keep a baseline for each setup you repeat. Use a compact reference such as Band A, seated version, approved low anchor, then record the actual repetitions for that visit. If the anchor or variation changes, start a new comparison rather than carrying the earlier number forward as a target.'
      ], example: { title: 'Illustrative band-session context', headers: ['Entry', 'Equipment context', 'Comparison'], rows: [['First visit', 'Band A, familiar seated setup', 'Baseline for this setup'], ['Second visit', 'Band B, same named movement', 'Different equipment; separate context'], ['Third visit', 'Band A, original setup', 'Review against first visit']], caption: 'A record-keeping example, not attachment instructions or a resistance prescription.' } },
      { id: 'organised-history', title: 'Keep the routine and equipment reference together', paragraphs: [
        'Use a consistent session name so you can find previous band workouts without searching through every home session. Review the equipment reference before starting, and log actual completed sets rather than leaving the planned set count untouched. Correct mistaken band identities while you still remember what you used.',
        'Nexal on Android offers free custom workouts, manual workout logging and history. Those tools can support the routine record; this guide does not promise a dedicated band-resistance calculator or anchor field. Premium AI planning remains optional, and suggested movements still need checking against your equipment and instruction.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See Nexal home workout planning and logging', text: 'Use free workout history for completed sessions and keep your band setup reference available.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/how-to-improve-strength-flexibility/', label: 'NHS: resistance bands as strength activity' }, { href: '/ai-workout-planner', label: 'Nexal: free tracking and Premium planning' }],
    faqs: [
      { question: 'Can I compare bands using colour alone?', answer: 'Keep the manufacturer, product line and band style as well as the colour. Bands from different sets should not be assumed equivalent.' },
      { question: 'Does Nexal calculate the exact resistance of my band?', answer: 'A dedicated band-resistance calculator is not a verified feature. Preserve equipment context and avoid inventing a fixed load.' }
    ]
  },
  {
    slug: 'workout-log-sets-reps-weight-units',
    title: 'Workout log units: recording sets, reps and weight clearly',
    metaTitle: 'Workout Log: Sets, Reps and Weight Units',
    description: 'Understand set-by-set entries, kilograms versus pounds, bar totals and per-hand loads with practical workout logging examples.',
    publishedAt: '2026-10-11', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A workout entry such as 3 x 10 at 20 is only useful if you know what each number means. This guide is for anyone cleaning up a log that mixes units, planned repetitions and different equipment conventions.',
    takeaway: 'Record completed sets separately when results differ, keep units explicit and define what the load includes. Never turn an ambiguous old entry into a confident new target.',
    sections: [
      { id: 'sets-and-reps', title: 'Distinguish the plan from individual set results', paragraphs: [
        'The NHS describes a repetition as one complete movement and a set as a group of repetitions. A programme instruction of three sets of ten is therefore an intention to complete three groups. It does not prove that every group actually contained ten repetitions during your session.',
        'If the completed results were ten, nine and eight, record those three results. Writing three by ten erases the difference between intention and completion. Before changing any exercise instruction, ask the programme author what to do with the result. The logging task is to preserve it accurately, not to decide a new workload.'
      ] },
      { id: 'unit-labels', title: 'Keep kilograms and pounds visibly separate', paragraphs: [
        'Use the unit displayed on the equipment, or convert it explicitly using a reliable conversion reference. NIST identifies the kilogram, with symbol kg, as the SI unit of mass. In gym conversation this is commonly described as weight, but the important logging habit is to retain the unit beside the number.',
        'A stack marked 40 lb and one marked 40 kg should not become identical entries reading 40. If you train at gyms with different labels, keep a unit reference for each equipment setup. Check the app field before entering the number; this guide does not assume Nexal automatically detects or converts equipment units.'
      ] },
      { id: 'load-includes', title: 'State what is included in the load', paragraphs: [
        'For a barbell, decide whether your record includes the bar, and confirm the actual equipment specification. In an arithmetic example, a bar marked 15 kg with a 5 kg plate on each side totals 25 kg. Recording only the plates would produce 10 kg for the same setup and make later comparisons unreliable.',
        'For dumbbells, preserve whether the number is per hand. For machines, use the marked value with the machine identity rather than assuming it represents a directly comparable free-weight load. A consistent equipment convention prevents clerical changes from appearing to be performance changes.'
      ], example: { title: 'Illustrative load conventions', headers: ['Setup', 'Clear interpretation'], rows: [['15 kg bar plus two 5 kg plates', '25 kg including bar'], ['Two dumbbells marked 8 kg', '8 kg per hand'], ['Machine selector marked 4', 'Setting 4; mass unknown']], caption: 'Arithmetic and notation examples only. Confirm the markings on the equipment you actually use.' } },
      { id: 'unilateral-and-warmups', title: 'Label per-side counts and preparation sets', paragraphs: [
        'For one-sided work, note whether ten means ten on each side or ten across both sides. If the sides differ, preserve both completed results in the available fields or a companion note. Adding them into one total may hide the information your programme author needs.',
        'Also distinguish any preparation sets from the work sets described by your programme. Otherwise a later review may compare a session with preparation included against one without it. Choose an explicit convention and keep it for the whole block. You are defining the record, not deciding how many preparation sets somebody should perform.'
      ] },
      { id: 'repair-and-log', title: 'Repair ambiguous entries without inventing history', paragraphs: [
        'When an older entry lacks a unit or load convention, mark the uncertainty in your reference notes. Do not silently convert it from memory several weeks later. Start a clear baseline at the next suitable session and compare forward from that point. An honest gap is preferable to a fabricated progression.',
        'Nexal includes core manual workout logging, custom workouts and history for free on Android. Test one familiar session and inspect the saved results. Keep any context that the available fields cannot express in a companion record. Premium AI planning is a separate option, not a requirement for maintaining a clear log.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore free workout logging in Nexal', text: 'Record your completed routine and review workout history on Android.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/how-to-improve-strength-flexibility/', label: 'NHS: definitions of sets and repetitions' }, { href: 'https://www.nist.gov/pml/owm/si-units-mass', label: 'NIST: kilogram and mass units' }, { href: '/ai-workout-planner', label: 'Nexal: free workout tracking' }],
    faqs: [
      { question: 'Should I log the planned reps if I completed fewer?', answer: 'Log the repetitions you completed. Keep the original instruction available separately so intention and result remain distinguishable.' },
      { question: 'Does a barbell entry include the bar?', answer: 'Define the convention and confirm the bar specification. Including the bar in a total is clear when that convention is explicitly preserved.' }
    ]
  },
  {
    slug: 'exercise-substitutions-comparable-history',
    title: 'Tracking exercise substitutions without mixing up your history',
    metaTitle: 'Exercise Substitutions: Keep Clear History',
    description: 'Record why an exercise changed, distinguish alternative equipment and build separate baselines for useful workout comparisons.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'An unavailable machine or a programme update can leave your history full of exercises that share a nickname but describe different work. This guide helps regular gym users document substitutions while keeping the original programme and completed session understandable.',
    takeaway: 'Name the exercise you actually performed, preserve the reason for the change and compare each variation with its own earlier entries.',
    sections: [
      { id: 'permission-to-substitute', title: 'Establish which alternatives are suitable beforehand', paragraphs: [
        'Ask the person who designed your routine which alternatives are appropriate when equipment is unavailable. Keep the agreed list alongside the programme. Similar-looking movements are not automatically interchangeable, and a shared muscle-group label does not tell you whether an alternative fits your instructions or circumstances.',
        'For a practical planning example, your coach might provide a preferred movement and an approved alternative for the same session slot. Record both names in the planning reference, but only log the one you perform. If pain is the reason for stopping, seek appropriate advice rather than treating equipment substitution as an injury assessment.'
      ] },
      { id: 'name-the-actual', title: 'Use the performed variation as the history identity', paragraphs: [
        'If the plan says machine press and you perform a coach-approved dumbbell variation, leave the completed record under the dumbbell variation. Keeping the old exercise name while changing only the load makes the history look continuous when the equipment has changed. That confusion carries into every later comparison.',
        'Include meaningful distinctions such as seated versus standing, supported versus unsupported, or the specific machine when relevant. Avoid adding a new exercise identity for trivial spelling changes. The useful rule is that the name should distinguish a setup change that would matter when reconstructing the session.'
      ] },
      { id: 'separate-baselines', title: 'Start a separate baseline for each substitute', paragraphs: [
        'Imagine the preferred machine entry reads a marked 30 kg and the replacement uses 10 kg dumbbells per hand. Neither multiplying the dumbbells nor matching the machine label produces a reliable conversion between the exercises. Keep both resistance conventions intact and review each against its own previous use.',
        'If this is your first time logging the approved substitute, it is a baseline entry. Do not manufacture a previous result from the preferred movement. At a later session you can compare the same substitute, equipment and instructions, while still recognising that its placement in the session may affect the context.'
      ], example: { title: 'Illustrative substitution history', headers: ['Visit', 'Performed movement', 'Comparison reference'], rows: [['1', 'Preferred machine variation', 'Earlier entries on that machine'], ['2', 'Approved dumbbell variation', 'First entry for this variation'], ['3', 'Preferred machine variation', 'Visit 1 and earlier matching entries']], caption: 'No load equivalence or exercise suitability is implied by this example.' } },
      { id: 'record-reason', title: 'Keep temporary changes distinct from programme revisions', paragraphs: [
        'Write a short reason in a companion record when necessary: preferred machine occupied, approved alternative used once. That is different from a permanent programme revision starting on a given date. Without the reason, the next visit may accidentally repeat a one-off change as though the whole programme had been rewritten.',
        'After several visits, review which substitutions recur. If the same station is unavailable every Tuesday, bring that concrete pattern to your coach. They can help decide whether a permanent change is appropriate. Your contribution is the attendance and equipment evidence, rather than a guess that the alternative must be better.'
      ] },
      { id: 'nexal-substitution-record', title: 'Use custom workouts as a manual record of decisions', paragraphs: [
        'Nexal on Android offers free custom workouts, manual workout logging and history. Use those features to record an existing routine and the actual exercises completed. Keep the approved alternatives list separately if you need context beyond the available workout fields. This workflow does not rely on an automatic substitution engine.',
        'Before the next visit, check the original programme, any dated revisions and the last matching entry. That brief check prevents a temporary equipment workaround becoming an unexplained permanent change. Premium AI planning is optional when you want help organising a routine, but generated suggestions still require review for suitability.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See custom workout tracking in Nexal', text: 'Keep actual exercise variations in free workout history, with optional Premium AI planning.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: workout logging and AI planning' }],
    faqs: [
      { question: 'Should substitutes share one exercise history?', answer: 'Keep meaningfully different variations distinguishable and compare each with matching earlier entries. A shared informal name does not establish equivalent resistance.' },
      { question: 'Can I swap an exercise because it hurts?', answer: 'Stop and seek appropriate guidance for pain or concerning symptoms. This record-keeping method does not determine which replacement is safe for you.' }
    ]
  },
  {
    slug: 'track-supersets-clearly',
    title: 'How to track supersets clearly, one exercise at a time',
    metaTitle: 'How to Track Supersets Clearly',
    description: 'Keep paired exercises, round numbers and completed results readable without combining different movements into one ambiguous workout entry.',
    publishedAt: '2026-10-11', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'If your existing programme pairs two exercises, a single line called superset can hide what happened. This guide is for people already following paired exercise instructions who need to record rounds without mixing resistance, repetitions or interruptions.',
    takeaway: 'Keep each movement as its own result, use round labels in your reference record and distinguish planned pairing from what you actually completed.',
    sections: [
      { id: 'read-pair-instructions', title: 'Clarify the pairing before choosing notation', paragraphs: [
        'Read the original programme and confirm which exercises are paired, the intended order and the rest instructions. For the recording examples here, a pair is labelled A1 and A2 and a round means one pass through both. That notation defines the example; your coach may use different terminology or instructions.',
        'Do not assume paired exercises require zero transition time or an identical repetition count. Ask the programme author to clarify any ambiguity. Good notation preserves the instructions you have been given rather than replacing them with a rule copied from another routine.'
      ] },
      { id: 'separate-exercise-entries', title: 'Record each exercise under its own identity', paragraphs: [
        'Keep A1 and A2 recognisable by their full exercise names. A combined entry reading 20 kg, 20 reps cannot explain whether those repetitions belong to one movement, both movements or a mixture. Equipment and resistance may differ between the pair, so each needs an independent completed result.',
        'Use a companion round reference if the tracker does not expose grouping fields. You can still log the exercise results in Nexal without claiming a dedicated superset mode. The extra notation tells you how the entries were paired; the workout history preserves the completed work for each movement.'
      ] },
      { id: 'round-example', title: 'Preserve unequal results within a round', paragraphs: [
        'Imagine an illustrative record with A1 completed for ten repetitions and A2 for eight in round one. Round two contains nine and eight. Write the actual values beside the appropriate exercise rather than calling both rounds ten plus ten. A tidy-looking repeated number is less useful than an accurate uneven result.',
        'If only A1 happens in round three because the other station becomes unavailable, leave A2 incomplete. Two completed movements in the earlier rounds do not establish completion in the third. Keep the interruption reason in your reference so the next review does not confuse it with a deliberate programme change.'
      ], example: { title: 'Illustrative paired-exercise record', headers: ['Round', 'A1 completed reps', 'A2 completed reps'], rows: [['1', '10', '8'], ['2', '9', '8'], ['3', '9', 'Not performed; station unavailable']], caption: 'Invented results to show notation, not suggested repetition counts or a training programme.' } },
      { id: 'order-and-rest', title: 'Keep order and rest context alongside the pair', paragraphs: [
        'When comparing an exercise with previous history, check whether it was paired in both sessions. A standalone entry and an entry performed after another movement do not describe the same session context. Preserve the order and any meaningful departures from the prescribed rest instructions instead of treating all matching exercise names as identical.',
        'Do not subtract walking time between stations from your memory and present the remainder as an exact rest measurement. If you timed a defined interval, say what you timed. If the pair was interrupted by a conversation or queue, a brief interruption note may explain the session more honestly than a fabricated stopwatch value.'
      ] },
      { id: 'gym-practicalities', title: 'Keep the pairing practical in a shared gym', paragraphs: [
        'Check gym rules and avoid assuming you can reserve two busy stations. Ask your coach how to handle a pairing when the equipment cannot be used as intended. Record the arrangement you actually followed, including an approved change to separate exercises, instead of preserving the planned pairing label after the sequence changed.',
        'Nexal offers free manual workout logging, custom workouts and history on Android. That is enough to test a clear exercise-by-exercise recording workflow. Premium AI planning is optional. A useful first test is whether you can reconstruct one real paired session from the saved results and your short round reference.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal workout recording', text: 'Use free workout logging for each completed movement and keep pairing context in your own reference.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: free workout tracking and custom routines' }],
    faqs: [
      { question: 'Does this guide require a Nexal superset mode?', answer: 'No dedicated superset mode is assumed. Log movements individually and keep round or pairing context in a companion record where needed.' },
      { question: 'Should I combine the repetitions of both exercises?', answer: 'Keep separate results for each movement. A combined number hides differences in equipment, load and completion.' }
    ]
  },
  {
    slug: 'record-workout-rest-times',
    title: 'Recording workout rest times without confusing the interval',
    metaTitle: 'Recording Workout Rest Times Clearly',
    description: 'Define what your rest timer measures, distinguish planned and actual breaks, and record useful context without chasing false precision.',
    publishedAt: '2026-10-11', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'A rest-time entry is hard to interpret if one session measures from the end of a set and another measures from when you pick up your phone. This guide helps people following established rest instructions make their timing records consistent.',
    takeaway: 'Define the start and end of the interval, preserve the programme instruction separately and record interruptions honestly. Timing is context, not a reason to rush the next set.',
    sections: [
      { id: 'define-interval', title: 'Define what starts and stops the clock', paragraphs: [
        'Choose a clear recording convention that matches the programme instructions. For example, you might time from completing one set to beginning the next set of the same exercise. Write that definition in your reference. Another person may record only a seated break, which is a different interval even if both call it rest.',
        'For paired exercises, ask whether the instruction refers to the transition between movements or the interval after completing the pair. Keep those intervals distinguishable. A number such as ninety seconds has little meaning without the event it follows and the event it precedes.'
      ] },
      { id: 'planned-versus-observed', title: 'Keep the intended interval separate from the measured one', paragraphs: [
        'If your programme specifies a rest interval, preserve it as the instruction. The actual interval belongs to the session record. A queue, equipment adjustment or forgotten timer can make those values different. Copying the planned interval into every completed record would hide that difference.',
        'Suppose an illustrative programme reference says ninety seconds, while a measured interval is two minutes ten seconds because the seat needed adjustment. Record the measured interval and the setup reason when useful. This example is about interpreting records, not recommending either rest duration for your training.'
      ] },
      { id: 'timer-method', title: 'Use a simple timer method you can repeat', paragraphs: [
        'A separate phone stopwatch can support timing if your workout log does not have a suitable field or timer. Keep the phone somewhere safe and use it between efforts rather than while handling equipment. This guide does not claim a built-in Nexal rest timer, notifications or background timing behaviour.',
        'Before a real session, test your method once so you know how to start, stop and read the interval. If tapping the timer adds friction, decide with your programme author whether an approximate record is sufficient for the question you are investigating. More decimal places do not repair an inconsistent measurement method.'
      ] },
      { id: 'interruptions', title: 'Label interruptions instead of guessing exact values', paragraphs: [
        'If you forget to start the stopwatch, write not measured in the companion record. If the interval includes a long equipment wait, label that wait. Do not replace a missing observation with the usual rest instruction. A missing value communicates uncertainty; an invented familiar value disguises it.',
        'For example, a short reference might read interval one measured; interval two interrupted by a queue; interval three not measured. You can still record completed sets and repetitions accurately. Timing uncertainty does not make the rest of the workout history useless, and it need not turn the session into an elaborate data collection task.'
      ], bullets: ['Retain the planned instruction.', 'State the timed events.', 'Mark estimates and missing measurements.', 'Add an interruption reason only when it explains the record.'] },
      { id: 'interpretation', title: 'Use rest context when comparing sessions', paragraphs: [
        'When completed repetitions differ, check rest context alongside exercise variation, resistance and order. A longer interval is a difference worth noticing, but it does not by itself prove the reason for a better result. Avoid changing several programme variables merely to make the log appear to improve.',
        'Nexal includes free manual workout logging, custom workouts and history on Android. Keep your completed session there and use a companion timing reference for details the supported fields cannot express. If you want a generated routine, Premium AI planning is optional; any rest instructions still need review against your circumstances and appropriate guidance.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See Nexal workout tracking and AI planning', text: 'Keep completed workouts in free history; use your own timing method where needed.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: workout tracking and Premium planning' }],
    faqs: [
      { question: 'Is a Nexal rest timer required for this workflow?', answer: 'No. A built-in rest timer is not assumed. Use a separate stopwatch and companion record if needed.' },
      { question: 'Should I enter the usual rest time if I forgot to measure?', answer: 'Mark it as not measured or clearly estimated. Keep the usual programme instruction separate from the observed result.' }
    ]
  },
  {
    slug: 'track-missed-workouts-without-doubling',
    title: 'Tracking missed workouts without doubling the next session',
    metaTitle: 'Track Missed Workouts Without Doubling Up',
    description: 'Keep missed appointments separate from workout history, record partial sessions honestly and make a clear next-session decision.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A missed gym appointment creates two separate questions: how to record the gap and what to do next. This guide helps people with an existing programme answer the first accurately and take a clear, informed question to the programme author for the second.',
    takeaway: 'Keep missed appointments in the planning record and completed work in workout history. A gap is information about the week, not an instruction to combine sessions.',
    sections: [
      { id: 'appointment-status', title: 'Record the missed appointment outside completed history', paragraphs: [
        'Use your calendar or companion planning record to mark an appointment as not attended. Do not create a completed workout containing the planned values if no training happened. That would make attendance counts and later exercise comparisons inaccurate. The diary should distinguish intention from an event that actually took place.',
        'For example, Wednesday Session B may be marked missed because a work meeting ran late. Keep the reason brief and practical. You do not need an emotional score or a long justification. The useful information is which appointment failed and whether its cause is likely to recur.'
      ] },
      { id: 'partial-versus-missed', title: 'Distinguish a shorter session from no session', paragraphs: [
        'If you attended and completed some exercises, log those results on the actual date. Keep the unperformed part visibly unperformed in your planning reference. A partial session is neither a complete version of the original plan nor an empty day; preserving the difference makes the next discussion much easier.',
        'Imagine a visit ending after two of five planned exercises because you had to leave. Record the two completed movements and their results. Avoid entering zero resistance for the other exercises as though you performed unloaded sets, since zero can describe an actual equipment choice rather than absence.'
      ] },
      { id: 'next-decision', title: 'Use an agreed rule for the next appointment', paragraphs: [
        'Ask your coach or programme author what to do when a session is missed: resume a sequence, skip a particular appointment or revise the calendar. The appropriate choice depends on the programme and your circumstances. An app streak or an unfinished checklist does not decide that choice for you.',
        'Write the agreed decision down with its date. For example, a coach might tell you which session to perform on Saturday after a missed Wednesday appointment. Follow that specific guidance instead of merging both exercise lists into one larger visit because they are still visible on the calendar.'
      ], example: { title: 'Illustrative record after a disrupted week', headers: ['Event', 'Where to record it', 'What to preserve'], rows: [['Missed appointment', 'Calendar or planning note', 'Date, session identity, brief reason'], ['Partly completed visit', 'Workout history plus planning note', 'Actual results and unperformed items'], ['Next-session decision', 'Programme reference', 'Agreed instruction and effective date']], caption: 'This organises records; it does not prescribe a catch-up schedule.' } },
      { id: 'review-patterns', title: 'Look for scheduling patterns across several weeks', paragraphs: [
        'Review missed appointments by slot rather than simply counting them. If three late shifts repeatedly interrupt Thursday evenings, the useful problem is that Thursday is unreliable. Moving the appointment may be worth discussing before changing exercise volume or deciding you need a completely different programme.',
        'Keep the denominator honest. If four visits were planned and three occurred, that is three of four planned appointments for that week. If only two visits were planned during travel, compare those two appointments with what happened. Avoid making weeks with different availability look equivalent through a raw visit total.'
      ] },
      { id: 'restart-recording', title: 'Make the next record a fresh factual entry', paragraphs: [
        'At the next suitable visit, inspect the last matching completed session and the agreed next-session instruction. Log what happens that day without backdating it to fill the earlier gap. The original missed appointment should remain in the planning history so you can assess whether the new schedule helped.',
        'Nexal provides free manual workout logging, custom workouts and history on Android. Use it for completed visits and maintain appointment status separately when needed. Automatic catch-up planning is not assumed. Premium AI generation is optional and does not remove the need to review changes with appropriate guidance.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal workout history', text: 'Keep an honest record of completed sessions with free manual tracking on Android.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: core workout logging and optional planning' }],
    faqs: [
      { question: 'Should I log a missed workout as completed with zero weight?', answer: 'Keep missed appointments in your planning record. Zero weight can describe performed work and should not be used as a substitute for did not attend.' },
      { question: 'Should I combine two sessions after missing one?', answer: 'Do not let an unfinished checklist decide the workload. Ask the programme author how to proceed and record the agreed next-session decision.' }
    ]
  },
  {
    slug: 'workout-app-shared-gym-equipment',
    title: 'Using a workout app when gym equipment is shared',
    metaTitle: 'Workout Logging in a Busy Shared Gym',
    description: 'Prepare for equipment queues, identify machine settings and log the session that happened without assuming gym bookings or live availability.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A workout plan may look straightforward until the only suitable bench is occupied. For people training in busy shared gyms, the useful app workflow is quick reference and accurate logging, supported by a clear plan for equipment constraints.',
    takeaway: 'Prepare approved alternatives, keep equipment identities visible and record queues or order changes when they matter. Workout logging does not reserve a station.',
    sections: [
      { id: 'before-arrival', title: 'Prepare the session before entering the busy floor', paragraphs: [
        'Review your routine before arrival so you know the required equipment and can recognise any constraints. Ask your trainer in advance which alternatives or order changes are appropriate. A ready reference reduces the temptation to improvise a replacement simply because an unfamiliar machine happens to be free.',
        'Keep that reference short: preferred station, approved alternative if any, and the question to ask staff if neither is available. Gym etiquette and booking rules differ, so check the actual facility policy. A personal workout app should not be treated as permission to claim equipment.'
      ] },
      { id: 'machine-identities', title: 'Identify the machine as well as the exercise', paragraphs: [
        'Two stations labelled row may have different handles, adjustments and marked resistance systems. Keep a recognisable equipment identifier in your companion reference, such as the manufacturer and model visible on the station. Do not photograph other gym users or assume a location nickname will still make sense after the room is rearranged.',
        'Record the familiar seat or support setting where relevant and follow facility instruction for setup. If the machine changes, make the change visible in your comparison record. A selector marked 6 on one station should not automatically be treated as the same load as setting 6 on another.'
      ] },
      { id: 'wait-or-change', title: 'Use the approved decision when a station is occupied', paragraphs: [
        'If the preferred station is busy, use the approach agreed with your programme author and the gym rules. That might involve waiting, an approved alternative or a suitable order change. This guide does not prescribe which option fits your programme, and a shorter queue does not establish exercise suitability.',
        'For an illustrative record, you may note that the planned second exercise was performed fourth after a queue. Keep the actual exercise results under their proper identities. The order note helps explain the session context without pretending the queue itself was part of the original training design.'
      ] },
      { id: 'fast-recording', title: 'Log between efforts without blocking the station', paragraphs: [
        'Open the relevant custom workout and check the next exercise before you start. Enter completed results while they are fresh, then follow the facility rules for returning equipment and moving on. Keep the phone out of the way while handling weights or adjusting machines. A useful log should fit the visit rather than create another equipment bottleneck.',
        'If you cannot enter a result immediately, keep a brief temporary record and reconcile it before finishing the visit. Mark any forgotten detail as uncertain instead of guessing. This workflow does not assume offline operation, wearable control or automatic set detection; check the app in the environment where you intend to use it.'
      ] },
      { id: 'review-availability', title: 'Use repeated equipment problems as planning evidence', paragraphs: [
        'After several visits, review whether the same station repeatedly disrupts the same time slot. A record of three interrupted Tuesday visits gives your trainer a concrete constraint to work with. It is more useful than describing the whole gym as always busy, and it may support a discussion about appointment time or programme organisation.',
        'Nexal on Android includes free custom workouts, manual workout logging and history. Try those features during a familiar session to see whether the workflow suits the gym. Premium AI planning is optional. Live machine availability, equipment reservations and gym booking integrations are not verified Nexal features and are not required for this approach.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'See Nexal workout logging for gym sessions', text: 'Keep your routine and completed results accessible through free tracking on Android.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: gym planning and free workout logging' }],
    faqs: [
      { question: 'Can Nexal tell me which gym machines are free?', answer: 'Live equipment availability is not a verified feature. This guide uses your own observation and a manually prepared routine.' },
      { question: 'What should I record when the exercise order changes?', answer: 'Keep the actual exercise results and preserve the order change with its reason in a companion reference if needed.' }
    ]
  },
  {
    slug: 'compare-workout-progress-beyond-weight',
    title: 'Comparing workout progress beyond the weight lifted',
    metaTitle: 'Compare Workout Progress Beyond Weight',
    description: 'Review repetitions, comparable setups and attendance alongside resistance, using a worked example that avoids misleading workout totals.',
    publishedAt: '2026-10-11', category: 'WORKOUT TRACKING', readTime: '5 min read',
    intro: 'If the resistance number stays the same, your workout history can still contain useful information. This guide is for people reviewing an established routine who want a more careful comparison than heavier weight equals progress.',
    takeaway: 'Compare matching exercise conditions and inspect several observations. More repetitions, clearer execution notes and reliable attendance answer different questions; none alone proves every kind of progress.',
    sections: [
      { id: 'choose-question', title: 'Decide what question the comparison should answer', paragraphs: [
        'Start with a specific question: did I complete the instructed repetitions, did I attend the available appointments, or did I repeat the same equipment setup? These are different questions and need different records. A combined session total cannot reliably answer all of them at once.',
        'Write one review question beside the programme block. For example, can I consistently complete the currently prescribed work under the same setup? That gives the log a purpose without creating a new exercise target. Your programme author can help interpret the observations when a decision about progression is needed.'
      ] },
      { id: 'match-context', title: 'Check the comparison conditions before the numbers', paragraphs: [
        'Match the exercise variation, equipment, load convention and session context. A row performed first and a different row performed near the end are not clean substitutes for one another. Keep meaningful changes in order, rest or setup visible so that a numerical difference does not appear more conclusive than it is.',
        'For example, two entries both saying 12 kg may refer to per-hand dumbbells on one day and a combined load on another. Resolve that convention first. If you cannot resolve it, use the newer clear entry as a baseline instead of building a trend from ambiguous historical numbers.'
      ] },
      { id: 'repetition-example', title: 'Compare completed repetitions at the same recorded load', paragraphs: [
        'Consider an illustrative exercise performed under matching conditions at a recorded 12 kg per hand. One visit contains completed repetitions of eight, eight and seven; another contains eight, eight and eight. The second record contains one more completed repetition across those sets. That is the factual observation.',
        'It is not automatically proof that the next session should use heavier equipment. The original instructions, execution and wider context still matter. If the later visit instead has four sets, simply adding all repetitions would compare different amounts of work. Keep the set structure visible before calling a larger total an improvement.'
      ], example: { title: 'Illustrative comparison under matching conditions', headers: ['Visit', 'Recorded load', 'Completed reps', 'Observation'], rows: [['A', '12 kg per hand', '8, 8, 7', '23 across three sets'], ['B', '12 kg per hand', '8, 8, 8', '24 across three sets'], ['C', '12 kg per hand', '8, 8, 8, 8', 'Different set count; separate context']], caption: 'Invented numbers for interpreting a log, not prescribed loads, sets or progression rules.' } },
      { id: 'other-observations', title: 'Keep process observations specific and modest', paragraphs: [
        'Useful process notes might say remembered the correct seat setting or completed the session without searching for the exercise list. These describe familiarity and organisation. Avoid converting them into claims about measured strength, muscle gain or technique quality. An app entry does not independently assess how a movement was performed.',
        'Attendance is another separate observation. Completing three of three planned appointments tells you something about schedule execution; it does not measure the quality of each repetition. If a coach gives you a specific execution cue, keep that feedback attributed to the coach and ask how they want it reviewed.'
      ] },
      { id: 'review-in-history', title: 'Review a few matching entries before changing the plan', paragraphs: [
        'Pick a small set of comparable visits and look for repeated patterns rather than judging the programme from one good or difficult day. Bring the actual entries and any missing context to your coach. Keep conclusions proportional to the data: recorded repetition completion is a clearer claim than universal fitness improvement.',
        'Nexal provides free manual workout logging, custom workouts and history on Android. Use history as the source for your review and calculate any comparison yourself where needed. This guide does not assume an automatic performance score or readiness recommendation. Premium AI planning is optional and does not replace interpreting the actual records.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal workout history and planning', text: 'Use free workout history to review completed sessions in their proper context.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: free workout logging and history' }],
    faqs: [
      { question: 'Is more total lifted weight always a better session?', answer: 'A larger total may reflect more sets, a different exercise or a changed load convention. Compare matching conditions and the purpose of your programme first.' },
      { question: 'Can I track useful observations without increasing resistance?', answer: 'Yes. Completed repetitions, attendance and specific setup observations can all be recorded, but they describe different aspects of the routine.' }
    ]
  },
  {
    slug: 'first-gym-session-logging-checklist',
    title: 'A first gym session logging checklist for new members',
    metaTitle: 'First Gym Session: Logging Checklist',
    description: 'Prepare a simple first-visit record, capture taught equipment settings and check saved results without turning an induction into a data task.',
    publishedAt: '2026-10-11', category: 'GYM BASICS', readTime: '5 min read',
    intro: 'Your first gym visit may involve an induction, equipment explanations and plenty of unfamiliar names. A small logging checklist helps preserve what you learned without expecting you to build a programme or collect every possible measurement on day one.',
    takeaway: 'Arrange instruction, learn the equipment identity and save an honest record of the work you actually perform. The first log is a memory aid, not a performance test.',
    sections: [
      { id: 'before-first-visit', title: 'Confirm the visit purpose before opening the tracker', paragraphs: [
        'Check whether the appointment is an induction, a coaching session or independent training with a routine already provided. Ask the facility what to bring and who will explain unfamiliar equipment. A logging app cannot replace that introduction, and a list of exercise names does not show you how to use a station.',
        'Prepare only the information you already know, such as the appointment time and the routine supplied by an instructor. Leave unknown loads and settings blank in your planning reference. Filling them with guesses before you arrive can make them look like instructions you are supposed to follow.'
      ] },
      { id: 'learn-identities', title: 'Capture the names and settings you are taught', paragraphs: [
        'Ask the instructor for the exercise name and how to recognise the relevant machine. If they set a seat or support position for you, record the identifier they explain. Keep a companion reference when a detail does not fit the workout fields. Settings help you remember instruction; they are not evidence that you can use every similar-looking machine identically.',
        'Check the unit on the equipment before entering a resistance value. If a dial shows a numbered setting without a unit, preserve it as a setting instead of inventing kilograms. Ask staff when the marking is unclear. It is much easier to clarify the equipment while standing beside it than to reconstruct it next week.'
      ] },
      { id: 'first-results', title: 'Enter only completed work during the visit', paragraphs: [
        'Record the actual exercise, completed sets and repetitions, and marked resistance where relevant. If the instructor changes the plan during the induction, your final history should reflect the work performed. Keep their instructions for next time separately so they are not confused with the current results.',
        'For example, an induction might include a demonstration and then one attempt by you. Do not log the instructor demonstration as your own set. Likewise, if you only observe a machine explanation, that belongs in your learning reference rather than as a completed exercise in your workout history.'
      ], bullets: ['Confirm which movement you performed.', 'Check the equipment unit and load convention.', 'Record your completed result rather than the demonstration.', 'Keep the next-session instruction separate.'] },
      { id: 'after-visit-check', title: 'Check the saved record before details fade', paragraphs: [
        'After the visit, open the saved workout and compare it with your short reference. Check the date, exercise names, units and any accidentally duplicated entries. Correct clerical errors while you can still distinguish a machine explanation from an exercise you actually performed.',
        'Write down one unresolved question for the next visit, such as how to recognise the alternative station. Avoid turning the review into a judgement about how strong you should be. On a first visit, a readable record and a clear follow-up question are practical outcomes you can control.'
      ] },
      { id: 'first-app-test', title: 'Test the free tracker with one real session', paragraphs: [
        'Nexal is available on Android and includes core manual workout logging, custom workouts and history for free. Use a familiar, instructed session to test whether you can enter and find results comfortably. Keep the phone safely aside during movements and equipment adjustments, and follow the gym rules for phone use.',
        'You do not need Premium AI generation simply to record the routine supplied at your induction. If you later want help organising a plan, review that optional feature after learning the recording workflow. Any generated routine still needs checking; unfamiliar exercises are a reason to ask for instruction rather than rely on app text alone.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Try Nexal’s free workout logging', text: 'Record your first instructed session on Android before considering optional AI planning.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: Android availability and free tracking' }],
    faqs: [
      { question: 'Should I enter weights before my first induction?', answer: 'Do not invent loads. Keep unknown details open until the instructor explains the suitable equipment and programme.' },
      { question: 'Do I need Premium to record my first gym visit?', answer: 'No. Core manual workout logging, custom workouts and history are free in Nexal on Android.' }
    ]
  },
  {
    slug: 'maintain-coach-programme-in-workout-app',
    title: 'Maintaining a coach-provided programme in a workout app',
    metaTitle: 'Log a Coach-Provided Workout Programme',
    description: 'Transfer your coach’s routine manually, preserve programme versions and prepare clear completed-session records for your next review.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'If a coach already writes your programme, an app can serve as your personal record rather than another source of training instructions. The main challenge is transferring the routine accurately and keeping later changes traceable.',
    takeaway: 'Keep the coach’s original as the instruction source, manually record actual work and date every programme revision. Ask about ambiguity before silently rewriting the plan.',
    sections: [
      { id: 'original-reference', title: 'Keep the original programme available', paragraphs: [
        'Retain the version your coach supplied, including the date or block identifier. A custom workout is a convenient working copy, but it should not become the only source for instructions you may have abbreviated. Keep any exercise cues, rest instructions and permitted alternatives accessible in the original reference.',
        'Before transferring anything, identify the session names and the meaning of any shorthand. Ask whether a repetition range applies per side, whether a load includes the bar and which exercises are paired. A ten-minute clarification can prevent several weeks of records built around a mistaken interpretation.'
      ] },
      { id: 'manual-transfer', title: 'Check the working copy line by line', paragraphs: [
        'Create the session list manually using the supported custom workout workflow. Compare each exercise identity and its instructions against the original. Do not substitute a similarly named movement merely because it is easier to find. If the app fields cannot express a cue, retain it in the programme reference instead of assuming it has been preserved elsewhere.',
        'For example, a coach might distinguish two supported row variations across sessions A and B. Keep those identities distinct in your working copy. After entering both sessions, read back the original and the app list together. This is an accuracy check, not a request to redesign the programme.'
      ] },
      { id: 'completed-results', title: 'Treat the workout log as the performed record', paragraphs: [
        'During the session, enter completed results rather than treating planned values as confirmed. If the programme gives a range, record the repetitions actually completed in each set. Preserve useful deviations, such as an approved equipment alternative, in a companion record if the available logging fields do not capture the context.',
        'Imagine a session instruction containing three work sets, but an appointment forces you to leave after two. The history should contain the two completed sets. Your coach can then see the difference between an exercise difficulty and a scheduling interruption when you discuss the session.'
      ] },
      { id: 'dated-revisions', title: 'Give every revision an effective date', paragraphs: [
        'When the coach updates the routine, record which session changes and when the new version starts. Keep earlier completed sessions associated with the old instructions. Replacing the current working copy does not mean earlier history should be interpreted as though the revised programme existed at the time.',
        'A compact change record could say Block 3 begins 26 October; Session B uses the new coach-approved movement. If a message is ambiguous, ask whether the change applies immediately or at the next block. That detail matters more than inventing a polished programme title.'
      ], example: { title: 'Illustrative programme version record', headers: ['Version', 'Effective date', 'Reference'], rows: [['Block 2', 'Earlier sessions', 'Original coach document'], ['Block 3', '26 October', 'Updated Session B instruction'], ['Completed history', 'Actual visit dates', 'Results under the version then in use']], caption: 'This can be kept in your own notes; no coach portal or automatic programme synchronisation is assumed.' } },
      { id: 'coach-review', title: 'Prepare a precise question for the next review', paragraphs: [
        'Before meeting your coach, inspect the relevant completed sessions and identify the question you need answered. Instead of saying the programme is confusing, point to a specific exercise, date and instruction. You can refer to the record during the conversation or manually summarise it in the communication method you already use.',
        'Nexal provides free custom workouts, manual workout logging and history on Android. This guide assumes manual entry and personal review, not an import, export, coach account or sharing integration. Premium AI workout planning is optional; you do not need another generated programme to record the one your coach has already provided.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore free custom workout logging', text: 'Use Nexal as a personal record of your existing coach-provided routine on Android.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: free tracking and optional AI workout generation' }],
    faqs: [
      { question: 'Can Nexal import my coach’s programme automatically?', answer: 'Automatic programme imports are not a verified feature. This guide uses manual custom workout entry and keeps the original programme as a reference.' },
      { question: 'Should I use AI planning alongside my coach’s instructions?', answer: 'You do not need AI planning to log a coach-provided routine. Discuss proposed programme changes with your coach rather than silently combining instructions.' }
    ]
  },
  {
    slug: 'workout-routine-changing-work-roster',
    title: 'Organising a workout routine around a changing work roster',
    metaTitle: 'Workout Routine for a Changing Work Roster',
    description: 'Map rotating shifts, separate session order from weekdays and keep workout dates clear when your work roster changes between weeks.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A fixed Monday workout label can become useless when your shifts rotate. This guide is for rostered workers who already have a suitable routine and need to organise it around changing availability without losing the session sequence.',
    takeaway: 'Plan from the published roster, preserve session identities and log actual dates. A free calendar window is a scheduling possibility, not proof that training there is appropriate.',
    sections: [
      { id: 'roster-map', title: 'Map availability after the roster is published', paragraphs: [
        'Write the work shifts, commute and other fixed responsibilities into your calendar first. Then identify possible gym windows with realistic arrival and departure times. Avoid building the week around a recurring appointment that conflicts with every second roster. The purpose is to expose the constraint before it becomes another missed visit.',
        'Keep practical context such as a late finish or an early return shift visible. This guide does not determine sleep needs, recovery or exercise suitability after a night shift. Ask the programme author how your particular schedule should affect session placement, and use appropriate professional advice when health concerns are involved.'
      ] },
      { id: 'sequence-identities', title: 'Use session identities that survive weekday changes', paragraphs: [
        'Label an existing programme with stable names such as Session A, Session B and Session C. Preserve the prescribed order and spacing in the programme reference. Assign dates only after reviewing the current roster and any guidance from the person who designed the routine.',
        'For example, A might fall on Tuesday in one roster and Thursday in another. It should remain A in the workout history, with the actual visit date attached. Otherwise the same session becomes scattered under Tuesday Workout, Thursday Workout and Day Off Workout, making comparison unnecessarily difficult.'
      ] },
      { id: 'rolling-window', title: 'Review the roster cycle rather than forcing identical weeks', paragraphs: [
        'If your availability changes across a two-week roster, review attendance across that actual cycle. One calendar week may contain fewer feasible appointments than the next. A raw weekly visit count can obscure whether the appointments you deliberately planned were completed. Keep both the intended slots and the outcomes.',
        'Imagine roster one offers Tuesday and Saturday windows, while roster two offers Thursday and Sunday. These are illustrative availability records, not a prescribed frequency or spacing. Compare the planned appointments with actual visits across the two rosters, then ask whether the schedule or the programme needs clarification.'
      ], example: { title: 'Illustrative roster planning reference', headers: ['Roster', 'Possible windows', 'Record separately'], rows: [['Week A', 'Tuesday and Saturday', 'Suitable session placement agreed beforehand'], ['Week B', 'Thursday and Sunday', 'Actual visit dates and completed work'], ['Roster revision', 'A shift changes', 'Updated appointment and reason']], caption: 'Availability examples only. Appropriate recovery and programme placement require individual consideration.' } },
      { id: 'last-minute-change', title: 'Keep a clear rule for late roster changes', paragraphs: [
        'Ask your coach what to do if an extra shift removes the next training window. Keep the agreed rule with the programme rather than making a new decision from an unfinished calendar each time. A late change should update the appointment record; it should not create a completed workout that did not happen.',
        'If a visit begins on one date and ends after midnight, choose a consistent date convention and retain the timing context in your own reference. Check the saved date instead of assuming the app groups overnight sessions exactly as you expect. Consistency prevents a single visit appearing to belong to two different roster days.'
      ] },
      { id: 'manual-roster-workflow', title: 'Pair your roster calendar with completed workout history', paragraphs: [
        'Keep intended appointments in the calendar you already use for shifts and completed results in the tracker. At each roster update, inspect the next programme instruction and the last matching session before filling possible windows. This separates the scheduling decision from the factual record of training.',
        'Nexal on Android includes free custom workouts, manual workout logging and history. Those features can support the session record while your calendar holds the roster. Automatic shift imports, calendar synchronisation and automatic schedule adjustments are not assumed. Premium AI planning is optional, and any generated timetable still needs checking against the real roster.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal tracking for your own routine', text: 'Keep stable session names and completed workout history free on Android.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: workout tracking and Premium AI planning' }],
    faqs: [
      { question: 'Will Nexal import my work roster?', answer: 'Roster imports and calendar synchronisation are not verified features. Use your own roster calendar alongside manual workout logging.' },
      { question: 'How do I compare sessions that move between weekdays?', answer: 'Keep a stable session identity and compare matching exercise records using the actual visit dates, rather than relying on the weekday name.' }
    ]
  },
  {
    slug: 'plan-shorter-gym-sessions',
    title: 'Planning shorter gym sessions with a clear time budget',
    metaTitle: 'Plan Shorter Gym Sessions: Time Budget Guide',
    description: 'Build a realistic gym time budget, identify setup delays and record shorter sessions without pretending omitted exercises were completed.',
    publishedAt: '2026-10-11', category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A forty-minute gap in your calendar is not necessarily forty minutes available for exercises. This guide helps time-limited gym users describe the real constraint, prepare a suitable shorter option and keep a truthful record when a session ends early.',
    takeaway: 'Budget the whole visit, agree priorities before arrival and log only completed work. Save time through preparation and appropriate planning rather than rushing prescribed rests or unfamiliar movements.',
    sections: [
      { id: 'whole-visit-budget', title: 'Start with the time you must leave', paragraphs: [
        'Work backwards from the appointment after the gym. Include leaving the floor, returning equipment, changing and travelling onward. Then subtract the time needed to arrive and get ready. The remaining window is the useful planning constraint to give your trainer, not the full space between calendar appointments.',
        'For an arithmetic example, a fifty-minute door-to-door window might include ten minutes travelling each way and five minutes changing or packing. That leaves twenty-five minutes inside the visit for the agreed preparation and exercise content. These numbers illustrate budgeting only; they do not establish a suitable session duration.'
      ] },
      { id: 'measure-bottlenecks', title: 'Observe where an ordinary session spends time', paragraphs: [
        'Record a few broad timestamps during a familiar visit: arrival, ready to begin, equipment wait and departure. Keep the method simple enough that timing does not become another delay. You are looking for practical bottlenecks such as searching for the exercise list, repeated equipment setup or waiting for a specific station.',
        'Distinguish those delays from preparation and rest that belong to your programme. Do not remove them just because they occupy minutes. Bring the observed time budget to the programme author so they can decide what a suitable shorter version contains. The log provides evidence for that decision.'
      ] },
      { id: 'approved-priorities', title: 'Agree a shorter version before the busy day', paragraphs: [
        'Ask which elements are priorities, what can be omitted if needed and whether a different session is appropriate for the available window. Keep the answer as a clearly labelled short version of your existing programme. Do not silently compress every exercise into fewer minutes by rushing technique, deleting rests or adding unapproved pairings.',
        'For example, a coach may provide Full Session A and a separate Time-Limited A with its own instructions. Preserve both identities and the date the shorter option was agreed. That makes it clear which plan you followed and prevents the short version gradually replacing the full one without discussion.'
      ] },
      { id: 'short-session-record', title: 'Record the shorter session on its own terms', paragraphs: [
        'Log the actual exercises, completed sets and repetitions, and resistance where relevant. If you follow an agreed shorter version, identify that version in your reference. If you unexpectedly leave early, record the completed work and preserve which items were unperformed. An unfinished list should not be copied into history as a completed full session.',
        'Compare repeated short-version sessions with other short-version sessions under matching conditions. A smaller session total than the full programme is not automatically a worse result; the planned content differed. The useful question is whether you completed the suitable version chosen for that appointment and kept the record accurate.'
      ], example: { title: 'Illustrative time-budget review', headers: ['Part of visit', 'Recording question'], rows: [['Travel and changing', 'How much of the calendar gap is already committed?'], ['Preparation and programme rests', 'What must remain according to the agreed routine?'], ['Equipment delays', 'What repeated constraint should the trainer know?'], ['Shorter version', 'Which instructions were agreed and actually completed?']], caption: 'A planning checklist, not a shortened workout prescription.' } },
      { id: 'reduce-admin', title: 'Reduce logging friction before changing the programme', paragraphs: [
        'Open the relevant routine before arrival and check the previous matching entry. Keep equipment conventions and any short-version instructions easy to find. Those administrative steps can remove confusion without deciding new exercise content. After the visit, check the saved results once while you still remember the session.',
        'Nexal includes free custom workouts, manual workout logging and history on Android. Use them to record your own full and time-limited routines. Premium AI workout planning is optional if organisation is the task you want help with, but automatic session compression is not assumed. Review any suggested routine against your real time budget and appropriate guidance.'
      ] }
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal custom workouts and planning', text: 'Record an agreed shorter routine with free tracking, or review optional Premium AI planning.' },
    sources: [{ href: '/ai-workout-planner', label: 'Nexal: custom workout tracking and AI planning' }],
    faqs: [
      { question: 'Should I shorten rests to fit every exercise?', answer: 'Do not let the calendar silently rewrite programme instructions. Ask the programme author for an appropriate shorter option and preserve its instructions.' },
      { question: 'Does Nexal automatically compress a workout to my available time?', answer: 'Automatic session compression is not a verified feature. This guide uses a manually agreed routine and completed-work logging.' }
    ]
  }
];
