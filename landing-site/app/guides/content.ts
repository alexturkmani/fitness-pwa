import { choosingGuides } from './choosing-guides';
import { trainingGuides } from './training-guides';
import { nutritionGuides } from './nutrition-guides';
import { goalGuides } from './goal-guides';
import { trainingDetailGuides } from './training-detail-guides';
import { foodDetailGuides } from './food-detail-guides';

export type GuideSection = {
  id: string;
  title: string;
  paragraphs: string[];
  bullets?: string[];
  example?: { title: string; headers: string[]; rows: string[][]; caption: string };
};
export type Guide = {
  contentBatch?: string;
  publishedAt?: string; updatedAt?: string;
  slug: string; title: string; metaTitle: string; description: string; category: string;
  readTime: string; intro: string; takeaway: string; sections: GuideSection[];
  feature: { href: string; label: string; text: string };
  sources: { href: string; label: string }[];
  faqs: { question: string; answer: string }[];
};

export const publishedDate = '2026-10-06';
export const libraryUpdatedDate = '2026-10-11';

export function guideDates(guide: Guide) {
  const published = guide.publishedAt || publishedDate;
  return { published, modified: guide.updatedAt || published };
}

export function guideTopic(guide: Pick<Guide, 'category'>) {
  if (/WORKOUT|HOME|GYM/.test(guide.category)) return 'WORKOUT PLANNING';
  if (/NUTRITION|MEAL/.test(guide.category)) return 'NUTRITION TRACKING';
  return 'CHOOSING AN APP';
}

const originalGuides: Guide[] = [
  {
    slug: 'beginner-3-day-workout-plan',
    title: 'A beginner three-day workout week you can actually organise',
    metaTitle: 'Beginner 3-Day Workout Plan: Gym Week & Tracking',
    description: 'Organise a beginner three-day gym week, keep a simple workout log and understand how AI planning differs from free workout tracking in Nexal.',
    category: 'WORKOUT PLANNING', readTime: '5 min read',
    intro: 'A useful workout plan answers three questions: when will you train, what will you practise, and how will you remember what you did? Start with a manageable week before worrying about complicated splits.',
    takeaway: 'Choose a schedule you can repeat. Keep the session details visible. Treat an example plan as a starting point, not a prescription for your body.',
    sections: [
      { id: 'schedule', title: 'Start with your calendar, not a six-day split', paragraphs: [
        'For someone learning their way around a gym, three available training days can make a clear weekly structure. Monday, Wednesday and Friday is one example, not a requirement. A two-day routine may fit your life better; more sessions are not automatically better sessions.',
        'The NHS recommends muscle-strengthening activity on at least two days each week as part of general adult activity guidance. That does not mean every person should follow the same exercises, intensity or timetable. Your experience, recovery and any health conditions matter.',
        'Pick realistic time slots and arrange an induction if you are unfamiliar with the equipment. Ask a qualified trainer to help you select movements and learn technique before adding challenging loads.',
      ] },
      { id: 'example-week', title: 'An illustrative three-day beginner gym week', paragraphs: [
        'This example shows how to organise sessions and notes. It is not an individual training programme and does not prescribe weights, sets or repetitions. Choose the actual exercises and workload with appropriate instruction.',
      ], example: { title: 'Example calendar: training and recovery', headers: ['Day', 'Session focus', 'What to record'], rows: [
        ['Monday', 'Full-body session: practise a squat, push and pull pattern', 'Exercises, completed sets, repetitions and technique notes'],
        ['Wednesday', 'Full-body session: revisit familiar movements and practise a hinge pattern', 'What felt comfortable and any changes agreed with your trainer'],
        ['Friday', 'Full-body session: repeat suitable movements and review the week', 'Completed work, recovery and next week’s available days'],
      ], caption: 'Illustrative organisation only. Leave room between sessions for recovery; adapt the schedule to your needs.' }, bullets: [
        'Before each session: follow the warm-up and technique guidance appropriate to your chosen movements.',
        'Between sessions: note how you feel rather than treating every free day as another hard workout.',
        'If an exercise causes pain or concerning symptoms, stop and seek appropriate advice rather than trying to complete the log.',
      ] },
      { id: 'workout-log', title: 'Keep a workout log that helps your next session', paragraphs: [
        'An exercise name alone is not much use next week. Record the variation you performed, the resistance used where relevant, and the sets and repetitions you completed. A short note such as “needed help setting up the machine” can be more useful than chasing a personal record.',
        'Compare like with like. Changing the exercise, equipment and technique every session makes a simple load comparison less meaningful. Ask your trainer when it is appropriate to progress rather than increasing weight because a graph looks flat.',
        'In Nexal, core workout logging, custom workouts and workout history are available without a subscription. Use those tools to keep the plan and your actual sessions together. You do not need Premium simply to record a workout.',
      ] },
      { id: 'ai-plan', title: 'When an AI workout planner can help', paragraphs: [
        'An AI planner is useful when you want help organising exercises around your goal, experience, training days and home or gym setting. Be specific about what you can realistically do. A polished plan is still only helpful if you can follow it.',
        'Nexal Premium generates personalised four-to-six-week workout plans, including exercise sets, repetitions and rest times. AI generation is a paid feature; the free tracker and this educational example are not a free AI workout generator.',
        'Review any generated plan before using it. AI can make mistakes and is not a substitute for a qualified coach, injury assessment or medical advice. Revisit the plan if your circumstances change.',
      ] },
      { id: 'review', title: 'Review consistency before judging results', paragraphs: [
        'At the end of the week, check which sessions you actually completed, what was difficult to organise, and whether your next week needs a different schedule. This gives you a practical adjustment rather than a verdict on your fitness.',
        'For your first tracked week, success can simply mean learning the workflow and keeping clear notes. No app or example timetable can guarantee a particular body change or rate of progress.',
      ] },
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore Nexal’s AI workout planner', text: 'Log workouts for free. Upgrade only when you want personalised AI planning.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/how-to-improve-strength-flexibility/', label: 'NHS: strength and flexibility guidance' }],
    faqs: [
      { question: 'Do beginners have to train three days a week?', answer: 'No. Three days is an example schedule. Choose a suitable routine around your circumstances and seek qualified guidance when you need help with exercise selection or technique.' },
      { question: 'Can I log this workout week in Nexal for free?', answer: 'Yes. Core workout logging and custom workouts do not require Premium. AI-generated workout plans do require Premium.' },
    ],
  },
  {
    slug: 'how-to-track-calories-and-macros',
    title: 'How to track calories and macros without making logging complicated',
    metaTitle: 'How to Track Calories & Macros: A Worked Example',
    description: 'Learn calorie and macro logging with a worked serving-size example, common mistakes and a simple daily routine. Start tracking free in Nexal on Android.',
    category: 'NUTRITION TRACKING', readTime: '5 min read',
    intro: 'Food logging becomes clearer when you know what the numbers describe. The most useful first skill is matching the amount you eat to the serving size on the label, not finding a perfect daily macro split.',
    takeaway: 'Match the food entry, units and portion before comparing totals. Treat estimates as estimates and keep tracking optional and appropriate to your wellbeing.',
    sections: [
      { id: 'basics', title: 'Calories and macros describe different parts of the same food', paragraphs: [
        'Calories describe energy. Protein, carbohydrate and fat are macronutrients, usually recorded in grams. A food can contribute all three, so its protein figure does not replace its calorie figure.',
        'As a rough calculation, protein and carbohydrate each provide four kilocalories per gram, while fat provides nine. Label totals can differ from a simple calculation because of rounding and other components such as fibre. Use the actual label for the food you are logging rather than treating the formula as a perfect audit.',
        'This guide explains logging, not how many calories you should eat. Personal nutrition targets depend on individual circumstances. If you need advice about a condition, pregnancy or a difficult relationship with food, speak with a qualified healthcare professional.',
      ] },
      { id: 'serving-example', title: 'A worked example: convert per-100 g values to your portion', example: { title: 'Illustrative portion calculation', headers: ['Label entry', 'Per 100 g', 'For 150 g'], rows: [
        ['Energy', '200 kcal', '300 kcal'], ['Protein', '10 g', '15 g'], ['Carbohydrate', '25 g', '37.5 g'], ['Fat', '6 g', '9 g'],
      ], caption: 'Portion multiplier = 150 ÷ 100 = 1.5. This example is not a daily calorie or macro target.' }, paragraphs: [
        'Imagine a food label gives the values below per 100 grams and you eat 150 grams. Multiply each value by 1.5. These are invented numbers for teaching the calculation, not the nutrition values of a recommended food.',
        'If an entry is per serving instead, check what that serving means. Two servings are not automatically 200 grams. Also check whether a value is in kcal or kJ; copying the number without the unit can make the entry substantially wrong.',
      ] },
      { id: 'daily-routine', title: 'Build a simple daily logging routine', paragraphs: [
        'Choose the meal, identify the food and enter the amount you actually ate. Check whether the entry describes the food raw, cooked or prepared: the same weight can mean something different after cooking.',
        'For a mixed meal, logging the ingredients can be clearer than choosing a vaguely similar restaurant dish. Record the portion of the finished recipe you ate and avoid counting both the recipe and its ingredients again.',
        'When you repeat a meal, reuse a recent entry and adjust it if the portion or ingredients change. Nexal supports core meal, calorie and macro tracking without a subscription, including copying recent meals for faster logging.',
      ], bullets: ['Check the food description and serving unit.', 'Adjust the quantity to match your portion.', 'Include additions such as sauces when relevant.', 'Review the entry once rather than repeatedly chasing perfect precision.'] },
      { id: 'common-errors', title: 'Common macro-tracking mistakes worth checking', paragraphs: [
        'A mistaken serving size can matter more than the difference between two similar food database entries. Start by checking portions, units and whether the food is raw or cooked.',
        'Barcode scanning helps find packaged-food information, but you still need to check the product and portion. AI food estimates are estimates, especially when ingredients or portion sizes are unclear. Neither tool removes the need to review an entry.',
        'Do not interpret a single day’s total as a diagnosis or a promise of weight change. If logging becomes distressing or compulsive, pause and seek appropriate support. Tracking is a tool, not a requirement for taking care of yourself.',
      ] },
      { id: 'free-premium', title: 'Free macro tracking versus Premium meal planning', paragraphs: [
        'Tracking records what you ate. Planning helps organise what you might eat next. They are connected but they are not the same feature, and an app should make that distinction clear before you subscribe.',
        'Nexal’s free tools cover core manual tracking. Premium adds AI meal plans around your targets and preferences, meal substitutions, AI macro estimates and barcode scanning. Review generated meal ideas and independently check ingredients if you have allergies.',
        'Start by logging a familiar meal. You can decide later whether planning and scanning save enough time to justify a subscription.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore the free calorie and macro tracker', text: 'Keep calories and macros beside your workouts. Premium AI meal planning is optional.' },
    sources: [
      { href: 'https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/', label: 'NHS: how to read food labels' },
      { href: 'https://www.nhs.uk/live-well/eat-well/food-types/different-fats-nutrition/', label: 'NHS: energy from fat, protein and carbohydrate' },
    ],
    faqs: [
      { question: 'Do I need a subscription to track macros in Nexal?', answer: 'No. Core calorie and macro tracking are free. Premium is needed for AI planning, AI estimates and barcode scanning.' },
      { question: 'Should label calories exactly match the macro calculation?', answer: 'Not always. Rounding and components such as fibre can cause differences. Check the label, serving size and units before assuming an entry is wrong.' },
    ],
  },
  {
    slug: 'choose-workout-and-meal-planner-app',
    title: 'How to choose a workout and meal planner app for Android',
    metaTitle: 'Choose a Workout & Meal Planner App for Android',
    description: 'Compare free tracking, AI plans, food logging and subscription terms before choosing an Android workout and meal planner. Use this practical checklist.',
    category: 'APP BUYING GUIDE', readTime: '4 min read',
    intro: 'A feature list is only the beginning. The right fitness app needs to fit your actual routine, make everyday logging manageable and explain what you will pay for before you commit.',
    takeaway: 'Test your real workflow before subscribing: one workout, one meal and a review of your progress. Compare the features you will actually use.',
    sections: [
      { id: 'job', title: 'Decide whether you need tracking, planning or both', paragraphs: [
        'If you already have a coach or a routine you like, you may mainly need a place to record sessions and meals. Paying for AI plan generation is not automatically useful in that situation.',
        'If you struggle to organise your week, planning tools may matter more. Check what inputs they accept and what the output contains: exercise names alone are different from sessions with sets, repetitions and rest times.',
        'For nutrition, ask whether you want a diary, meal ideas or both. A calorie counter does not necessarily generate meal plans, and a meal-planning app does not necessarily make daily logging easy.',
      ] },
      { id: 'checklist', title: 'A practical checklist before your first subscription', paragraphs: [
        'Use the same checklist for each app rather than comparing screenshots alone. The table describes questions to test, not a ranking of competitors.',
      ], example: { title: 'Check the workflow, not just the marketing', headers: ['Area', 'Question to answer'], rows: [
        ['Free access', 'Can I log workouts and meals without starting a subscription?'],
        ['Workout plans', 'Does the planner support my experience, schedule and home or gym setting?'],
        ['Food logging', 'Can I change portions and reuse a meal without starting over?'],
        ['Progress', 'Can I find previous entries and understand what a chart represents?'],
        ['Billing', 'Are the local price, renewal period and trial terms clear?'],
        ['Support', 'Can I restore a purchase and find help if access does not update?'],
      ], caption: 'Open each app and test the tasks that matter to you. Feature availability and prices can change.' } },
      { id: 'nexal-fit', title: 'Where Nexal fits, and what requires Premium', paragraphs: [
        'Nexal is an Android app that brings workout and nutrition tracking together. Core meal, calorie, macro, workout and progress tracking are available without a subscription. Custom workouts and recent-meal copying support a repeatable daily routine.',
        'Premium adds AI workout plans, AI meal plans, substitutions, AI macro estimates and barcode scanning. Workout generation supports gym or home settings and different experience levels; meal planning uses your targets and preferences.',
        'That combination may suit someone who wants planning and logging in one place. It may not suit someone looking for an iPhone app or expecting AI guidance to replace individual professional advice. Nexal is currently available through Google Play for Android.',
      ] },
      { id: 'trial', title: 'Read the trial and renewal terms before subscribing', paragraphs: [
        'Check the actual price shown by Google Play for your country and chosen plan. An annual equivalent monthly price is not a monthly payment: the annual amount is charged according to the terms shown at checkout.',
        'Nexal advertises a fourteen-day free trial for eligible new subscribers. Eligibility and the applicable offer are determined through Google Play. Read the renewal amount and cancellation terms before confirming; do not assume every account or offer includes a trial.',
        'During a trial, test the features you subscribed for. Generate and review a plan, use it in your routine and decide whether it saves you time. Manage or cancel a Google Play subscription through Google Play. Uninstalling an app is not a cancellation workflow.',
      ] },
      { id: 'first-session', title: 'A useful first-session test', paragraphs: [
        'Choose a familiar meal and log it with the correct portion. Record a workout you understand, then find the entry again in your history. This tests whether the app helps with real daily tasks before you judge the design or advanced features.',
        'If you choose Premium, check that the plan reflects the settings you supplied. Look for clear instructions and review anything that seems unsuitable. Also reopen the app to confirm that your subscription access remains available.',
        'This guide is published by Nexal, so it is not an independent product review. It is intended to help you assess fit, understand feature boundaries and make an informed decision without claims that one app is best for everyone.',
      ] },
    ],
    feature: { href: '/workout-meal-planner-app', label: 'See Nexal’s workout and meal planning features', text: 'Try core tracking for free before deciding whether Premium tools fit your routine.' },
    sources: [{ href: 'https://support.google.com/googleplay/answer/7018481', label: 'Google Play: manage and cancel subscriptions' }],
    faqs: [
      { question: 'Is an annual fitness subscription always better value?', answer: 'Only if you use the app enough to justify the commitment. Compare the total charge and renewal terms, not just an equivalent monthly figure.' },
      { question: 'Is this an independent comparison of fitness apps?', answer: 'No. This guide is published by Nexal and explains a practical selection checklist alongside Nexal’s own feature boundaries.' },
    ],
  },
];

const acquisitionGuides: Guide[] = [
  {
    slug: 'myfitnesspal-alternative-android',
    title: 'Looking for a MyFitnessPal alternative on Android? Start with your routine',
    metaTitle: 'MyFitnessPal Alternative for Android: Nexal Guide',
    description: 'Considering a MyFitnessPal alternative? Compare free tracking, workout planning and Premium tools, then try Nexal on Android without subscribing.',
    category: 'APP COMPARISON', readTime: '5 min read',
    intro: 'Changing fitness apps only helps if the new one solves a problem you actually have. If you want workouts and food logging together, use this checklist to decide whether Nexal fits your routine.',
    takeaway: 'Nexal is an option for free core tracking with optional Premium AI workout and meal plans. It is not a claim that everyone should switch or that every competitor feature has an equivalent.',
    sections: [
      { id: 'reason-to-switch', title: 'What are you looking to change?', paragraphs: [
        'Write down your reason before you download another app. You might want training and nutrition in one place, less manual planning, or a free way to record daily meals and workouts. Those are different needs and lead to different comparisons.',
        'If your current app already works well, switching may add effort without adding value. Familiar saved foods, previous entries and an established routine are worth considering alongside new features.',
        'For someone starting from scratch, the useful question is simpler: can you log a normal day and find the information again without getting lost? Start there instead of comparing the number of features on a marketing page.',
      ] },
      { id: 'feature-comparison', title: 'MyFitnessPal and Nexal: compare the feature boundaries', paragraphs: [
        'MyFitnessPal offers Free, Premium and Premium+ plans. Its official documentation places faster logging tools such as barcode scanning in Premium, and meal planning in Premium+. That makes the plan tier important when comparing features, not just the app name.',
        'Nexal provides free core calorie, macro, meal, workout and progress tracking. Its Premium tier adds AI workout and meal plans, substitutions, AI food estimates and barcode scanning. Nexal’s scanner is not free, so it should not be presented as a free-scanner replacement.',
      ], example: { title: 'Selected features, not an exhaustive product ranking', headers: ['Feature', 'MyFitnessPal', 'Nexal'], rows: [
        ['Core food diary', 'Free tier; paid tiers add tools', 'Free core meal, calorie and macro tracking'],
        ['Barcode scanning', 'Premium according to official documentation', 'Premium'],
        ['Meal planning', 'Premium+ according to official documentation', 'Premium AI meal planning'],
        ['AI workout plans', 'Check the current product for your specific training needs', 'Premium plans for home or gym'],
        ['Platform fit', 'Check current availability for your devices', 'Android through Google Play'],
      ], caption: 'Checked 6 October 2026. Selected MyFitnessPal details come from its official tier guide; offers and regional availability may change. This is a Nexal-published comparison.' } },
      { id: 'trial-workflow', title: 'Test Nexal with one meal and one workout', paragraphs: [
        'Download Nexal from Google Play, create your account and complete the setup. You can start core tracking without taking out a subscription. Pick a familiar meal, enter the portion and check the calorie and macro totals.',
        'Then create or record a workout you already understand. Find it in the history and check whether the logging process fits how you train. This practical trial tells you more than choosing an app by its icon or a promotional chart.',
        'If your main reason for switching is help with planning, review the Premium features separately. Eligible new subscribers may have a fourteen-day trial; the actual offer and renewal terms appear in Google Play. Do not assume a trial applies to every account.',
      ] },
      { id: 'migration', title: 'Check what you would leave behind before switching', paragraphs: [
        'Do not assume your old food diary, exercise history or saved recipes will automatically transfer. This guide does not promise a MyFitnessPal import or account connection in Nexal. Keep any information you need using the options offered by your existing service.',
        'Trying a new app also does not cancel an existing subscription. Review subscriptions with the provider or store where you bought them. Avoid paying for overlapping tools simply because you stopped opening the older app.',
        'If integrations or a particular food database matter to you, test those requirements directly. We are not claiming Nexal has a larger database, better accuracy or the same integrations as MyFitnessPal.',
      ] },
      { id: 'decision', title: 'Choose the tool you will use, not a universal winner', paragraphs: [
        'Nexal may fit an Android user who wants daily training and nutrition tracking together and optional AI planning. MyFitnessPal may remain the right choice if its workflow and specific tools already suit you.',
        'Compare the local checkout price, the features included in that plan and the practical benefit to your week. No comparison can tell you whether a subscription is worthwhile without knowing what you use.',
        'This article is written by Nexal, not an independent reviewer. MyFitnessPal is a separate product and is not affiliated with Nexal. Start with the free workflow and make the upgrade decision on your own experience.',
      ] },
    ],
    feature: { href: '/workout-meal-planner-app', label: 'Explore Nexal’s combined workout and meal tracker', text: 'Try Nexal’s free tracking before deciding whether to switch.' },
    sources: [
      { href: 'https://support.myfitnesspal.com/hc/en-us/articles/34889191368077-The-difference-between-Free-Premium-and-Premium', label: 'MyFitnessPal: official Free, Premium and Premium+ comparison' },
      { href: 'https://support.google.com/googleplay/answer/7018481', label: 'Google Play: manage existing subscriptions' },
    ],
    faqs: [
      { question: 'Is Nexal a free MyFitnessPal alternative?', answer: 'Nexal’s core calorie, macro, meal and workout tracking are free. Premium AI planning and scanning are paid. It is an alternative workflow, not a promise to reproduce every MyFitnessPal feature.' },
      { question: 'Can I import my MyFitnessPal history into Nexal?', answer: 'This guide does not promise an automatic import. Check what data you need to retain before changing apps.' },
      { question: 'Does installing Nexal cancel my other fitness subscription?', answer: 'No. Manage any existing subscription with its provider or the store where you purchased it.' },
    ],
  },
  {
    slug: 'ai-meal-planner-with-macros',
    title: 'How to use an AI meal planner with macros and real food preferences',
    metaTitle: 'AI Meal Planner with Macros: A Practical Guide',
    description: 'Learn what to enter into an AI macro meal planner, how to review a plan and when to substitute meals. Explore Nexal Premium on Android.',
    category: 'AI MEAL PLANNING', readTime: '5 min read',
    intro: 'A meal plan is only useful if the meals fit your day. Before you generate one, make your targets and preferences clear, then review the output rather than assuming that AI has checked every detail.',
    takeaway: 'Use AI to organise meal ideas, not to diagnose your needs. Review portions, ingredients and practicality before following a plan.',
    sections: [
      { id: 'planning-vs-tracking', title: 'A macro meal planner is different from a food diary', paragraphs: [
        'A food diary records meals you have eaten. A meal planner suggests what you could eat next. If you already have meals you like, free tracking may be enough; planning tools are optional.',
        'Planning around macros means considering calories, protein, carbohydrate and fat together. It does not mean there is one correct macro ratio for everyone, or that meeting numbers alone makes a diet suitable.',
        'The NHS Eatwell Guide describes balance across food groups over a day or week. Use that wider context rather than treating a generated macro total as a complete assessment of nutrition. Individual targets and dietary advice may require a qualified professional.',
      ] },
      { id: 'inputs', title: 'Give the planner inputs you can actually use', paragraphs: [
        'Start with the targets and preferences supported by the app. Nexal Premium uses your calorie and macro targets alongside food preferences, restrictions and allergies to generate meal ideas. Those inputs are not a guarantee that every generated recipe is suitable.',
        'Review whether the resulting meals fit your shopping, cooking time and normal routine. This guide does not claim that Nexal automatically optimises grocery costs, delivers groceries or checks every ingredient label.',
      ], example: { title: 'Illustrative meal-planning brief', headers: ['Input or review step', 'Example'], rows: [
        ['Targets', 'Use your own appropriate calorie and macro targets, not numbers copied from this article'],
        ['Preferences', 'State the foods or dietary pattern you prefer where the app supports it'],
        ['Restrictions', 'Record relevant exclusions; independently check recipe ingredients'],
        ['Daily practicality', 'Review whether breakfast, lunch, dinner and snacks fit your real day'],
        ['Portions', 'Check the serving amount before treating the nutrition breakdown as your intake'],
      ], caption: 'An input checklist, not a recommended diet or an actual generated Nexal plan.' } },
      { id: 'review', title: 'Review the plan before you cook', paragraphs: [
        'Read the meals and serving sizes first. An unfamiliar ingredient, unrealistic preparation step or unsuitable portion is a reason to adjust the plan, not a reason to force your routine around it.',
        'Check nutrition estimates against the food you actually use. Different brands, recipes and portions can change the totals. AI output can contain errors; a precise-looking breakdown should not be mistaken for a laboratory measurement.',
        'For allergies, independently verify ingredients, labels and cross-contact information. Do not rely on an AI plan to establish that a meal is safe. For medical dietary needs, seek advice from an appropriately qualified healthcare professional.',
      ] },
      { id: 'substitutions', title: 'Make substitutions deliberately rather than chasing exact matches', paragraphs: [
        'If a meal does not fit your tastes or available ingredients, a substitute can help keep the plan practical. Nexal Premium offers meal substitutions, but the suggested replacement still needs review.',
        'Check both the food and the portion. Replacing one ingredient with another gram-for-gram does not necessarily keep calories or macros identical. It may also change allergens or preparation requirements.',
        'Once you eat the adjusted meal, log what you actually had. Planning and tracking work best when the diary reflects your real portion rather than an untouched example from the plan.',
      ] },
      { id: 'getting-started', title: 'Start in Nexal without confusing free and Premium features', paragraphs: [
        'Download Nexal for Android, create an account and complete the setup. Core manual meal, calorie and macro tracking are free. You can learn the logging workflow before deciding whether AI meal planning would help.',
        'Premium adds AI meal plans, substitutions, AI macro estimates and barcode scanning. Meal plans include breakfast, lunch, dinner and snacks. Review the current local price and any eligible trial offer in Google Play before subscribing.',
        'A useful first test is to generate one plan, review every meal and try the workflow with food you know. Decide whether it reduces planning effort without making your routine harder. There is no guaranteed weight-loss or muscle-gain outcome from a generated menu.',
      ] },
    ],
    feature: { href: '/ai-meal-planner', label: 'Explore Nexal’s AI meal planner', text: 'Track meals free. Add Premium AI planning when it helps your routine.' },
    sources: [
      { href: 'https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/the-eatwell-guide/', label: 'NHS: food-group balance in the Eatwell Guide' },
      { href: 'https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/', label: 'NHS: checking nutrition and serving information' },
    ],
    faqs: [
      { question: 'Is Nexal’s AI macro meal planner free?', answer: 'No. AI meal generation is a Premium feature. Core manual meal, calorie and macro tracking are free.' },
      { question: 'Can AI meal planning guarantee an allergy-safe menu?', answer: 'No. Independently check ingredients, labels and cross-contact information. Use qualified advice for medical dietary requirements.' },
      { question: 'Does a meal plan replace logging what I actually eat?', answer: 'No. Adjust your diary for the meals and portions you actually consume, especially after substitutions.' },
    ],
  },
  {
    slug: 'home-workout-planner-app',
    title: 'How to choose and use a home workout planner app on Android',
    metaTitle: 'Home Workout Planner App: Android Setup Guide',
    description: 'Plan home workouts around your space, equipment and schedule. Use this setup checklist and explore free logging plus Premium AI plans in Nexal.',
    category: 'HOME WORKOUTS', readTime: '5 min read',
    intro: 'Training at home changes the planning problem. Your available space, equipment and time matter more than a long exercise list. Start with those constraints before choosing a workout app or generating a plan.',
    takeaway: 'Choose a home-compatible workflow and review the suggested movements. An app cannot inspect your room, check your equipment or supervise your technique.',
    sections: [
      { id: 'constraints', title: 'List your home-training constraints first', paragraphs: [
        'Decide where you will exercise, when the space is available and which equipment you can use safely. A shared living room and a dedicated training area are different environments. A plan should not depend on equipment you do not own.',
        'Check that you have enough clear space for the movements you choose, and follow equipment instructions. For unfamiliar exercises, learn suitable technique from a qualified trainer rather than treating app text as personal supervision.',
        'The NHS provides examples of strength activities that can be done at home. Those examples show that training does not always require a gym membership, but they are not evidence that every home routine fits every person.',
      ] },
      { id: 'setup-checklist', title: 'A home workout setup checklist', paragraphs: [
        'Use this checklist when reviewing an app or an AI-generated plan. It deliberately focuses on the practical setup rather than prescribing a training intensity.',
      ], example: { title: 'Illustrative home-plan review', headers: ['Constraint', 'What to check'], rows: [
        ['Space', 'Can you perform the chosen movements safely in the area available?'],
        ['Equipment', 'Does each exercise match equipment you have and know how to use?'],
        ['Schedule', 'Are the planned days realistic for your week?'],
        ['Experience', 'Do you understand the movements, or need qualified instruction first?'],
        ['Recording', 'Can you log completed sets and repetitions without losing your place?'],
        ['Recovery', 'Does your schedule leave appropriate room to recover?'],
      ], caption: 'A planning checklist, not a personalised exercise programme or safety inspection.' } },
      { id: 'weekly-template', title: 'Organise the week without inventing a perfect routine', paragraphs: [
        'Choose time slots you can realistically keep. An example could be Monday, Wednesday and Friday sessions, with non-training days kept flexible. That is an organisational example, not a requirement to train three times a week.',
        'Name each session clearly and keep the actual exercise details in your plan or workout log. If a session does not happen, record that honestly and review whether the schedule was workable. Do not turn missed sessions into a reason to rush or double the workload.',
        'If you already have a suitable routine from a trainer, an app can be useful simply for recording it. You do not need an AI generator to keep a home workout history.',
      ] },
      { id: 'ai-home', title: 'What to check in an AI home workout plan', paragraphs: [
        'Choose the home setting where the app supports it and provide your goal, experience and realistic training days. Then read the output. If a suggested exercise requires unavailable equipment or unsuitable movements, revise the plan before following it.',
        'Nexal Premium supports home or gym workout generation and includes exercise sets, repetitions and rest times in four-to-six-week plans. This does not promise every possible equipment combination, a no-equipment programme for every user or a plan tailored to an injury.',
        'AI plans can make mistakes. If you have an injury, health condition or uncertainty about exercise suitability, get appropriate professional guidance. Stop if a movement causes pain or concerning symptoms rather than completing it for a streak.',
      ] },
      { id: 'first-log', title: 'Try the home-workout logging workflow for free', paragraphs: [
        'Install Nexal from Google Play, create your account and complete the setup. Core workout logging and custom workouts are available without subscribing. Record a familiar, suitable session and check that you can find it again in your history.',
        'Keep clear notes about the exercise variation and completed work so that the next session is easier to organise. Review your training history alongside meal and progress tracking if keeping those in one app is useful to you.',
        'If planning is the task you want help with, compare the Premium offer after testing the free tracker. The local price and any trial eligibility are shown through Google Play. Start with a useful daily workflow, not an expectation that downloading an app guarantees results.',
      ] },
    ],
    feature: { href: '/ai-workout-planner', label: 'Explore home and gym workout planning in Nexal', text: 'Keep your home workouts organised with free tracking and optional Premium AI plans.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/strength-exercises/', label: 'NHS: examples of strength exercises at home' }],
    faqs: [
      { question: 'Can I track home workouts in Nexal without Premium?', answer: 'Yes. Core workout logging and custom workouts are free. AI workout generation requires Premium.' },
      { question: 'Does choosing a home setting guarantee no-equipment exercises?', answer: 'No. Review the generated exercises against your actual equipment and space before following a plan.' },
      { question: 'Can a home workout app replace technique instruction?', answer: 'No. An app cannot supervise your form or inspect your environment. Seek qualified guidance when you need it.' },
    ],
  },
];

const workflowGuides: Guide[] = [
  {
    slug: 'track-homemade-meals-and-meal-prep-macros',
    title: 'How to track homemade meals and meal-prep macros',
    metaTitle: 'Track Homemade Meals & Meal Prep Macros on Android',
    description: 'Calculate meal-prep portions with a worked recipe example, avoid double-counting and log homemade meals with free calorie and macro tracking in Nexal.',
    category: 'MEAL PREP TRACKING', readTime: '5 min read',
    intro: 'A homemade lunch rarely comes with a nutrition label for your exact portion. You can still make a useful estimate by recording the ingredients, calculating the batch totals and dividing them by the amount you actually eat.',
    takeaway: 'Estimate the whole batch first, then your share of it. Keep raw and cooked entries consistent and do not log both the finished dish and its ingredients twice.',
    sections: [
      { id: 'ingredients', title: 'Start with the ingredients you actually used', paragraphs: [
        'Write down the ingredients and their quantities before dividing the meal into containers. Include additions such as cooking oil, sauces or toppings when they form part of the food you eat. A generic entry for a similar dish may not match your recipe.',
        'Use the ingredient label or an appropriate nutrition entry, then scale its values to your quantity. The NHS explains that packaged-food labels may list nutrition per 100 grams, per 100 millilitres or per portion. Check which basis you are using instead of copying a number without its unit.',
        'A raw ingredient and its cooked version are not interchangeable just because both entries are measured in grams. Match the quantity you measured to the type of entry. Cooking can change the food’s water content and weight, so keep a clear record of your method.',
      ] },
      { id: 'worked-example', title: 'A worked meal-prep portion calculation', paragraphs: [
        'Imagine your ingredient calculations add up to 2,000 kcal, 120 grams of protein, 240 grams of carbohydrate and 62 grams of fat for a batch. If it is divided into four equal portions, each gets one quarter of those totals.',
        'If the containers are not equal, divide by the measured finished batch weight instead. In this example, a 1,600-gram batch gives a 400-gram portion a 25% share. A 300-gram portion has an 18.75% share. This proportional method assumes the ingredients are distributed evenly.',
      ], example: { title: 'Illustrative batch totals and portion shares', headers: ['Nutrition', 'Whole batch', '400 g portion (25%)', '300 g portion (18.75%)'], rows: [
        ['Energy', '2,000 kcal', '500 kcal', '375 kcal'],
        ['Protein', '120 g', '30 g', '22.5 g'],
        ['Carbohydrate', '240 g', '60 g', '45 g'],
        ['Fat', '62 g', '15.5 g', '11.625 g'],
      ], caption: 'Invented values for arithmetic only, not an actual recipe or a recommended meal. Rounded input values and uneven ingredients limit precision.' } },
      { id: 'uneven-portions', title: 'Check whether the portions really contain the same mix', paragraphs: [
        'The finished-weight method works best when the ingredients are mixed evenly. If one container gets more of a protein ingredient and another gets more sauce, equal container weights do not establish equal nutrition.',
        'For meals with separate components, record the component portions instead of assuming the entire meal has one uniform composition. Keep the process manageable: you are building an estimate, not performing a laboratory analysis.',
        'Recipe calculations can also overstate intake if you include ingredients that are discarded or not consumed. Be honest about that uncertainty rather than presenting the final number as exact.',
      ] },
      { id: 'log-in-nexal', title: 'Log the portion in Nexal, not the whole batch', paragraphs: [
        'Download Nexal for Android, create your account and complete the setup. Core manual meal, calorie and macro tracking are free. Use the supported manual logging workflow to record the nutrition for the portion you ate, not every container you prepared.',
        'You can calculate batch and portion totals outside the app and then log your portion. This article does not promise an automatic recipe-import or batch-weight calculator in Nexal. If you prefer to record ingredients separately, do not also add a full-meal entry for the same food.',
        'When you repeat the same meal, copying a recent meal can save time. Review the quantity and nutrition if you changed the ingredients or portion. A reused entry should be a helpful starting point, not an assumption that every lunch is identical.',
      ] },
      { id: 'planning-next-week', title: 'Separate meal-prep tracking from AI meal planning', paragraphs: [
        'Tracking tells you what you ate; planning suggests what to prepare next. Nexal Premium adds AI meal ideas, substitutions, AI macro estimates and barcode scanning. None of those tools makes a homemade-food estimate perfectly accurate.',
        'Try free logging first to see whether the diary fits your routine. If planning is the time-consuming part, review the Premium offer and any eligible trial in Google Play. Independently check ingredients and food labels when allergies or dietary restrictions matter.',
        'This is an educational logging method, not a calorie target or a weight-loss programme. For individual dietary needs, seek appropriate professional advice. If detailed tracking negatively affects your wellbeing, you do not need to keep doing it.',
      ] },
    ],
    feature: { href: '/calorie-macro-tracker', label: 'Explore free meal and macro tracking in Nexal', text: 'Keep homemade meals beside your workout and progress tracking.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/how-to-read-food-labels/', label: 'NHS: nutrition labels and portion information' }],
    faqs: [
      { question: 'How do I calculate macros for one meal-prep portion?', answer: 'Estimate the batch totals, then multiply by your portion’s share of the batch. This assumes the ingredients are distributed evenly; otherwise log components separately.' },
      { question: 'Does Nexal automatically calculate a homemade recipe?', answer: 'This guide does not promise an automatic recipe calculator. Calculate your portion totals and use the supported manual logging tools.' },
      { question: 'Can I track homemade meals without subscribing?', answer: 'Yes. Core manual meal, calorie and macro tracking are free. Premium AI planning and food tools are optional.' },
    ],
  },
  {
    slug: 'workout-tracker-vs-ai-workout-planner',
    title: 'Workout tracker vs AI workout planner: which do you need?',
    metaTitle: 'Workout Tracker vs AI Planner: Which Do You Need?',
    description: 'Understand workout logging versus AI plan generation, compare what to record and try free workout tracking before choosing Nexal Premium on Android.',
    category: 'WORKOUT APP GUIDE', readTime: '4 min read',
    intro: 'A workout tracker remembers what you did. An AI workout planner proposes what to do next. You may need one, both or neither, depending on whether you already have a suitable training routine.',
    takeaway: 'Use free tracking when you have a routine to record. Consider paid planning when organising sessions is the problem, and review any generated programme before following it.',
    sections: [
      { id: 'tracker', title: 'What a workout tracker helps you record', paragraphs: [
        'A tracker provides a place to record exercises and completed work. Clear records can help you remember your last session, compare similar sessions and discuss your routine with a coach.',
        'For resistance training, distinguish a repetition from a set. The NHS describes a repetition as one complete movement and a set as a group of repetitions. A record should describe what you actually completed, not just copy the intended programme.',
        'Keep exercise variations and units clear. A weight entered without saying whether it is for one dumbbell or the whole exercise can be confusing later. This is a recording habit, not a reason to increase resistance or train through pain.',
      ] },
      { id: 'planner', title: 'What an AI workout planner adds', paragraphs: [
        'A planner uses inputs such as your goal, experience and available training days to organise sessions. The value is in creating a starting structure, not simply generating a longer list of exercises.',
        'Nexal Premium generates four-to-six-week workout plans for home or gym settings, with sets, repetitions and rest times. Core custom workouts and workout logging are available without a subscription.',
        'AI does not observe your technique, assess an injury or know whether a particular exercise is suitable for you. Review the output and seek qualified guidance when you need help selecting movements or deciding how to train.',
      ] },
      { id: 'decision', title: 'Match the app feature to the problem you have', paragraphs: [
        'Use this checklist before paying for a planner. If you already have a suitable programme, buying another one may not solve the task you care about.',
      ], example: { title: 'Tracking or planning: a practical decision checklist', headers: ['Your situation', 'Start with', 'What to test'], rows: [
        ['A coach already provides your programme', 'A workout tracker', 'Can you clearly record and find completed sessions?'],
        ['You know your exercises but forget previous details', 'A workout tracker', 'Does your history make the next session easier to organise?'],
        ['You need help structuring the week', 'A planner plus tracking', 'Does the suggested schedule fit your actual availability?'],
        ['You are unsure about exercise safety or technique', 'Qualified guidance', 'Do not rely on AI output as a personal assessment'],
      ], caption: 'Feature-selection examples, not a personalised exercise recommendation.' } },
      { id: 'planned-vs-done', title: 'Keep planned work separate from completed work', paragraphs: [
        'Imagine a programme calls for three sets, but you complete two. Your history should reflect the two completed sets, with a note explaining any useful context. A plan is an intention; a workout log is a record.',
        'Before your next session, review the relevant exercise and session details rather than comparing unrelated totals. A different machine, exercise variation or movement range can make a direct comparison misleading.',
        'Use those notes to discuss adjustments with a trainer if needed. A chart or AI recommendation is not proof that a specific increase in workload is appropriate.',
      ] },
      { id: 'try-free', title: 'Try Nexal’s free workout tracker before deciding on AI', paragraphs: [
        'Install Nexal from Google Play and create your account. After setup, core workout tracking and custom workouts are available without subscribing. Record a familiar session, find the entry again and decide whether the workflow fits your training.',
        'If planning is the part you want help with, explore Premium after testing the tracker. Review the local price, the plan you are purchasing and any trial eligibility in the Google Play checkout. The advertised fourteen-day trial is for eligible subscribers, not a promise for every account.',
        'Nexal brings workouts, meals and progress together on Android, but the useful first outcome is modest: keeping one real session clear and accessible. Neither a tracker nor an AI plan guarantees a particular fitness result.',
      ] },
    ],
    feature: { href: '/ai-workout-planner', label: 'Compare Nexal’s free workout tracking and Premium planning', text: 'Record your routine free. Add AI planning only when you need it.' },
    sources: [{ href: 'https://www.nhs.uk/live-well/exercise/how-to-improve-strength-flexibility/', label: 'NHS: strength activity, sets and repetitions' }],
    faqs: [
      { question: 'Do I need AI planning to log workouts?', answer: 'No. Nexal’s core workout logging and custom workouts are free. Premium is needed for AI plan generation, not basic tracking.' },
      { question: 'Can I use Nexal to record a coach’s programme?', answer: 'You can use custom workouts and workout logging to record your routine. This does not promise an automatic coach-platform import or integration.' },
      { question: 'Does an AI planner replace a personal trainer?', answer: 'No. AI cannot supervise technique or provide an individual clinical assessment. Use qualified guidance when required.' },
    ],
  },
];

export const guides: Guide[] = [...originalGuides, ...acquisitionGuides, ...workflowGuides, ...choosingGuides, ...trainingGuides, ...nutritionGuides, ...goalGuides, ...trainingDetailGuides, ...foodDetailGuides];

export function findGuide(slug: string) { return guides.find((guide) => guide.slug === slug); }
