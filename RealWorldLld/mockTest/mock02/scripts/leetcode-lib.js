// Shared helpers for fetching a LeetCode problem and formatting it as a
// comment block, using the unofficial LeetCode public JSON/GraphQL endpoints.

const START_MARKER = "LEETCODE-PROBLEM-START";
const END_MARKER = "LEETCODE-PROBLEM-END";

const COMMENT_STYLES = {
  ".js": { line: "//" },
  ".ts": { line: "//" },
  ".java": { line: "//" },
  ".cpp": { line: "//" },
  ".c": { line: "//" },
  ".h": { line: "//" },
  ".py": { line: "#" },
  ".go": { line: "//" },
  ".rb": { line: "#" },
};

function commentStyleForExt(ext) {
  return COMMENT_STYLES[ext] || { line: "//" };
}

function decodeEntities(str) {
  return str
    .replace(/&nbsp;/g, " ")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&frasl;/g, "/");
}

function htmlToText(html) {
  let text = html
    .replace(/<sup>(.*?)<\/sup>/gi, "^$1")
    .replace(/<sub>(.*?)<\/sub>/gi, "_$1")
    .replace(/<\/li>/gi, "\n")
    .replace(/<li>/gi, "- ")
    .replace(/<br\s*\/?>/gi, "\n")
    .replace(/<\/p>/gi, "\n\n")
    .replace(/<p>/gi, "")
    .replace(/<\/pre>/gi, "\n")
    .replace(/<pre>/gi, "\n")
    .replace(/<[^>]+>/g, "");

  text = decodeEntities(text);
  text = text
    .split("\n")
    .map((l) => l.trim())
    .join("\n");
  text = text.replace(/\n{3,}/g, "\n\n").trim();
  return text;
}

function buildCommentBlock(problem, commentStyle) {
  const { line } = commentStyle;
  const bodyLines = htmlToText(problem.content).split("\n");

  const header = [
    `${line} ${START_MARKER}`,
    `${line} ${problem.questionFrontendId}. ${problem.title} [${problem.difficulty}]`,
    `${line} https://leetcode.com/problems/${problem.titleSlug}/`,
    `${line}`,
  ];

  const body = bodyLines.map((l) => (l.length ? `${line} ${l}` : line));

  const footer = [`${line} ${END_MARKER}`];

  return [...header, ...body, ...footer].join("\n");
}

async function fetchProblemSlugById(number) {
  const res = await fetch("https://leetcode.com/api/problems/all/");
  if (!res.ok) {
    throw new Error(`Failed to fetch problem list: ${res.status}`);
  }
  const data = await res.json();
  const match = data.stat_status_pairs.find(
    (p) => String(p.stat.frontend_question_id) === String(number)
  );
  if (!match) {
    throw new Error(`No LeetCode problem found with number ${number}`);
  }
  return match.stat.question__title_slug;
}

async function fetchProblemBySlug(slug) {
  const query = `
    query questionData($titleSlug: String!) {
      question(titleSlug: $titleSlug) {
        questionFrontendId
        title
        titleSlug
        difficulty
        content
      }
    }
  `;
  const res = await fetch("https://leetcode.com/graphql", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ query, variables: { titleSlug: slug } }),
  });
  if (!res.ok) {
    throw new Error(`Failed to fetch problem data: ${res.status}`);
  }
  const { data } = await res.json();
  if (!data || !data.question) {
    throw new Error(`No problem data returned for slug ${slug}`);
  }
  return data.question;
}

async function fetchProblemByNumber(number) {
  const slug = await fetchProblemSlugById(number);
  return fetchProblemBySlug(slug);
}

module.exports = {
  START_MARKER,
  END_MARKER,
  commentStyleForExt,
  htmlToText,
  buildCommentBlock,
  fetchProblemSlugById,
  fetchProblemBySlug,
  fetchProblemByNumber,
};
